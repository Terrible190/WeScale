using System;
using System.Collections.Generic;

namespace BD.Models;

public partial class Personatge
{
    public int IdPersonatge { get; set; }

    public string Nom { get; set; } = null!;

    public bool Seleccionable { get; set; }

    public string Imatge { get; set; } = null!;

    public string Icona { get; set; } = null!;

    public float Velocitat { get; set; }

    public float HpBase { get; set; }

    public float DanyFisicBase { get; set; }

    public float DanyMagicBase { get; set; }

    public float DefensaFisicaBase { get; set; }

    public float DefensaMagicaBase { get; set; }

    public float CriticBase { get; set; }

    public float CriticMultiplicadorBase { get; set; }

    public virtual ICollection<EfecteInvocacio> EfecteInvocacios { get; set; } = new List<EfecteInvocacio>();

    public virtual ICollection<Jugador> Jugadors { get; set; } = new List<Jugador>();

    public virtual ICollection<PersonatgeAccio> PersonatgeAccios { get; set; } = new List<PersonatgeAccio>();
}
