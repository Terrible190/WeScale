using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class Accio
{
    public int IdObjActiu { get; set; }

    public string Nom { get; set; } = null!;

    public int Tipus { get; set; }

    public int? Cooldown { get; set; }

    public string? Descripcio { get; set; }

    public string? Imatge { get; set; }

    public string? Icona { get; set; }

    public int? Usos { get; set; }

    public int? Estadistica { get; set; }

    public int? NivellMinim { get; set; }

    public int? Tier { get; set; }

    public virtual ICollection<Efecte> Efectes { get; set; } = new List<Efecte>();

    public virtual ICollection<PersonatgeAccio> PersonatgeAccios { get; set; } = new List<PersonatgeAccio>();
}
