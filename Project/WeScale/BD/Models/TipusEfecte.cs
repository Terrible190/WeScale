using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class TipusEfecte
{
    public int IdTipusEfecte { get; set; }

    public int TipusEfecte1 { get; set; }

    public string? Imatge { get; set; }

    public string? Icona { get; set; }

    public virtual ICollection<Efecte> Efectes { get; set; } = new List<Efecte>();
}
