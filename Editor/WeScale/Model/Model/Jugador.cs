using System;
using System.Collections.Generic;

namespace Model.Model;

public partial class Jugador
{
    public int IdJugador { get; set; }

    public string Nom { get; set; } = null!;

    public int IdPersonatge { get; set; }

    public virtual Personatge IdPersonatgeNavigation { get; set; } = null!;
}
