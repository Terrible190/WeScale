using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class AccioEfecte
{
    public int IdAccioEfecte { get; set; }

    public int IdAccio { get; set; }

    public int IdEfecte { get; set; }

    public virtual Accio IdAccioNavigation { get; set; } = null!;

    public virtual Efecte IdEfecteNavigation { get; set; } = null!;
}
