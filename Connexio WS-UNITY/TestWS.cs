using UnityEngine;
using MikeSchweitzer.WebSocket;

public class TestWS : MonoBehaviour
{
    public WebSocketConnection ws;

    // 👇 TU SERVIDOR LOCAL
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
        // Leer mensajes entrantes
        while (ws.TryRemoveIncomingMessage(out string msg))
        {
            Debug.Log("📩 Recibido: " + msg);
        }

        // Enviar mensaje con SPACE
        if (Input.GetKeyDown(KeyCode.Space))
        {
            ws.AddOutgoingMessage("Hola servidor local 👋");
            Debug.Log("📤 Enviado");
        }
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