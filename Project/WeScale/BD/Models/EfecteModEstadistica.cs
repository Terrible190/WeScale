using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class EfecteModEstadistica
{
    public int IdEfecteMod { get; set; }

    public int IdEfecte { get; set; }

    public int NomStat { get; set; }

    public int Operacio { get; set; }

    public float Valor { get; set; }

    public virtual Efecte IdEfecteNavigation { get; set; } = null!;
}
