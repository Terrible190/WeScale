using System;
using System.Collections.Generic;

namespace Model.Model;

public partial class Estat
{
    public int IdEstat { get; set; }

    public string Nom { get; set; } = null!;

    public string? Imatge { get; set; }

    public string? Icona { get; set; }

    public virtual ICollection<EfecteEstat> EfecteEstats { get; set; } = new List<EfecteEstat>();
}
