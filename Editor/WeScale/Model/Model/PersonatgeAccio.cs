using System;
using System.Collections.Generic;

namespace Model.Model;

public partial class PersonatgeAccio
{
    public int IdPersonatgeAccio { get; set; }

    public int IdPersonatge { get; set; }

    public int IdObjhabarmActiu { get; set; }

    public bool Equipada { get; set; }

    public virtual Accio IdObjhabarmActiuNavigation { get; set; } = null!;

    public virtual Personatge IdPersonatgeNavigation { get; set; } = null!;
}
