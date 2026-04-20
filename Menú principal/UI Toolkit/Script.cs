using UnityEngine;
using UnityEngine.UIElements;
using MikeSchweitzer.WebSocket;
using System.Net.WebSockets;

public class Script : MonoBehaviour
{
    public UIDocument uiDocument;

    private Button btnStart;

    private WebSocketConnection ws;
    public string url = "ws://localhost:8080/ws";

    void Awake()
    {
        // WebSocket
        ws = gameObject.AddComponent<WebSocketConnection>();
        ws.StateChanged += OnStateChanged;
        ws.ErrorMessageReceived += OnError;
    }

    void Start()
    {
        // Conectar WS
        Debug.Log("Conectando a: " + url);
        ws.Connect(url);

        // UI Toolkit
        var root = uiDocument.rootVisualElement;

        btnStart = root.Q<Button>("btnStart");

        // Evento botón
        btnStart.clicked += OnStartClicked;
    }

    void Update()
    {
        // Leer mensajes del servidor
        while (ws.TryRemoveIncomingMessage(out string msg))
        {
            Debug.Log("📩 Recibido: " + msg);
        }
    }

    void OnStartClicked()
    {
        Debug.Log("🎮 Start pressed");

        string json = "{\"type\":\"startgame\",\"data\":null}";
        ws.AddOutgoingMessage(json);

        Debug.Log("📤 Enviado STARTGAME");
    }

    private void OnStateChanged(WebSocketConnection connection, MikeSchweitzer.WebSocket.WebSocketState oldState, MikeSchweitzer.WebSocket.WebSocketState newState)
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