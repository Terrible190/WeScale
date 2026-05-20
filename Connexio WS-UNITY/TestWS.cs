using UnityEngine;
using MikeSchweitzer.WebSocket;

public class TestWS : MonoBehaviour
{
    public WebSocketConnection ws;

    public string url = "ws://localhost:8080/ws";

    void Awake()
    {
        ws = gameObject.AddComponent<WebSocketConnection>();

        ws.StateChanged += OnStateChanged;
        ws.ErrorMessageReceived += OnError;
    }

    void Start()
    {
        Debug.Log("Conectando a: " + url);
        ws.Connect(url);
    }

    void Update()
    {
        // Leer mensajes
        while (ws.TryRemoveIncomingMessage(out string msg))
        {
            Debug.Log("📩 Recibido: " + msg);
        }

        // ENVIAR START GAME con SPACE
        if (Input.GetKeyDown(KeyCode.Space))
        {
            SendStartGame();
        }
    }

    void SendStartGame()
    {
        string json = "{\"type\":\"startgame\",\"data\":null}";
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