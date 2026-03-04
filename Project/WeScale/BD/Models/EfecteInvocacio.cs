using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class EfecteInvocacio
{
    public int IdEfecteInvo { get; set; }

    public int IdEfecte { get; set; }

    public int IdPersonatge { get; set; }

    public virtual Efecte IdEfecteNavigation { get; set; } = null!;

    public virtual Personatge IdPersonatgeNavigation { get; set; } = null!;
}
