using System;
using System.Collections.Generic;

namespace Model.Model;

public partial class Efecte
{
    public int IdEfecte { get; set; }

    public int IdTipusEfecte { get; set; }

    public int? TipusDany { get; set; }

    public int? Rang { get; set; }

    public int? Duracio { get; set; }

    public int IdObjArmHabActiu { get; set; }

    public string? Descripcio { get; set; }

    public virtual ICollection<EfecteEstat> EfecteEstats { get; set; } = new List<EfecteEstat>();

    public virtual ICollection<EfecteInvocacio> EfecteInvocacios { get; set; } = new List<EfecteInvocacio>();

    public virtual ICollection<EfecteModEstadistica> EfecteModEstadisticas { get; set; } = new List<EfecteModEstadistica>();

    public virtual Accio IdObjArmHabActiuNavigation { get; set; } = null!;

    public virtual TipusEfecte IdTipusEfecteNavigation { get; set; } = null!;
}
