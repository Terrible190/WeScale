using Assets.Scripts.Model;
using MikeSchweitzer.WebSocket;
using System;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UIElements;

namespace Assets.Scripts
{
    public class GameManager : MonoBehaviour
    {
        public UIDocument uiDocument;

        private Button btnStart;

        private WebSocketConnection ws;
        public string url = "ws://localhost:8080/ws";

        public BattleSystem battleSystem;
        public MapList data;
        public void OnJoinGame()
        {
            Debug.Log("Intentando unirse...");

            // aquí envías mensaje al servidor
            ws.AddOutgoingMessage("{\"type\":\"join\"}");
        }

     
        void HandleMessage(string raw)
        {
            JSONMessage message = JsonUtility.FromJson<JSONMessage>(raw);

            if (message == null)
            {
                Debug.LogWarning("Mensaje no parseable");
                return;
            }

            switch (message.messageType)
            {
                case "MAP_DATA":
                    MapList maps = JsonUtility.FromJson<MapList>(message.data);
                    HandleMapData(maps);
                    break;

                case "GAME_START":
                    SceneManager.LoadScene("BattleScene");
                    break;
            }
        }

        void HandleMapData(MapList data)
        {
            if (data == null)
            {
                Debug.LogError("❌ data es NULL");
                return;
            }

            if (data.mapas == null)
            {
                Debug.LogError("❌ data.mapas es NULL");
                return;
            }

            Debug.Log("Mapas recibidos: " + data.mapas.Length);

            foreach (var mapa in data.mapas)
            {
                Debug.Log("Mapa ID: " + mapa.id_mapa);
            }
        }

        public void StartGame()
        {
            if (ws.State != WebSocketState.Connected)
            {
                Debug.LogWarning("⚠️ Aún no conectado al servidor");
                return;
            }

            string json = "{\"messageType\":\"START_GAME\",\"data\":null}";
            ws.AddOutgoingMessage(json);

            Debug.Log("📤 Enviado STARTGAME");
        }
        void Awake()
        {
            ws = gameObject.AddComponent<WebSocketConnection>();

            ws.StateChanged += OnStateChanged;
            ws.ErrorMessageReceived += OnError;
            DontDestroyOnLoad(gameObject);

        }

        void Start()
        {
            Debug.Log("Conectando a: " + url);
            ws.Connect(url);
        }

        void Update()
        {
            while (ws.TryRemoveIncomingMessage(out string msg))
            {
                Debug.Log("📩 RAW: " + msg);

                HandleMessage(msg);
            }


        }


        void HandleGameState(string jsonData)
        {
            BattleDTO battle = JsonUtility.FromJson<BattleDTO>(jsonData);

            battleSystem.LoadFromServer(battle);
        }

        void SendStartGame()
        {
            string json = "{\"messageType\":\"START_GAME\",\"data\":null}";
            ws.AddOutgoingMessage(json);

            Debug.Log("📤 Enviado STARTGAME");
        }

        private void OnStateChanged(WebSocketConnection connection, WebSocketState oldState, WebSocketState newState)
        {
            Debug.Log($"🔌 Estado: {oldState} → {newState}");
        }

        private void OnError(WebSocketConnection connection, string error)
        {
            Debug.LogError("❌ Error WebSocket: " + error);
        }

        void OnDestroy()
        {
            ws?.Disconnect();
        }
    }
}