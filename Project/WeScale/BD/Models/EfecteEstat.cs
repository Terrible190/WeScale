using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class EfecteEstat
{
    public int IdEfecteEstat { get; set; }

    public int IdEfecte { get; set; }

    public int IdEstat { get; set; }

    public virtual Efecte IdEfecteNavigation { get; set; } = null!;

    public virtual Estat IdEstatNavigation { get; set; } = null!;
}
