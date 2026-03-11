using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Scaffolding.Internal;

namespace BD.Models;

public partial class AppDbContext : DbContext
{
    public AppDbContext()
    {
    }

    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Accio> Accios { get; set; }

    public virtual DbSet<Efecte> Efectes { get; set; }

    public virtual DbSet<EfecteEstat> EfecteEstats { get; set; }

    public virtual DbSet<EfecteInvocacio> EfecteInvocacios { get; set; }

    public virtual DbSet<EfecteModEstadistica> EfecteModEstadisticas { get; set; }

    public virtual DbSet<Estat> Estats { get; set; }

    public virtual DbSet<Jugador> Jugadors { get; set; }

    public virtual DbSet<Personatge> Personatges { get; set; }

    public virtual DbSet<PersonatgeAccio> PersonatgeAccios { get; set; }

    public virtual DbSet<TipusEfecte> TipusEfectes { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseMySql("server=localhost;database=wescale;uid=root", Microsoft.EntityFrameworkCore.ServerVersion.Parse("10.4.32-mariadb"));

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .UseCollation("utf8mb4_general_ci")
            .HasCharSet("utf8mb4");

        modelBuilder.Entity<Accio>(entity =>
        {
            entity.HasKey(e => e.IdObjActiu).HasName("PRIMARY");

            entity.ToTable("accio");

            entity.HasIndex(e => e.Nom, "nom").IsUnique();

            entity.Property(e => e.IdObjActiu)
                .HasColumnType("int(11)")
                .HasColumnName("id_obj_actiu");
            entity.Property(e => e.Cooldown)
                .HasDefaultValueSql("'0'")
                .HasColumnType("int(11)")
                .HasColumnName("cooldown");
            entity.Property(e => e.Descripcio)
                .HasMaxLength(1000)
                .HasColumnName("descripcio");
            entity.Property(e => e.Estadistica)
                .HasColumnType("int(11)")
                .HasColumnName("estadistica");
            entity.Property(e => e.Icona)
                .HasMaxLength(500)
                .HasColumnName("icona");
            entity.Property(e => e.Imatge)
                .HasMaxLength(500)
                .HasColumnName("imatge");
            entity.Property(e => e.NivellMinim)
                .HasColumnType("int(11)")
                .HasColumnName("nivell_minim");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .HasColumnName("nom");
            entity.Property(e => e.Tier)
                .HasColumnType("int(11)")
                .HasColumnName("tier");
            entity.Property(e => e.Tipus)
                .HasColumnType("int(11)")
                .HasColumnName("tipus");
            entity.Property(e => e.Usos)
                .HasColumnType("int(11)")
                .HasColumnName("usos");
        });

        modelBuilder.Entity<Efecte>(entity =>
        {
            entity.HasKey(e => e.IdEfecte).HasName("PRIMARY");

            entity.ToTable("efecte");

            entity.HasIndex(e => e.IdObjArmHabActiu, "id_obj_arm_hab_actiu");

            entity.HasIndex(e => e.IdTipusEfecte, "id_tipus_efecte");

            entity.Property(e => e.IdEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte");
            entity.Property(e => e.Descripcio)
                .HasMaxLength(1000)
                .HasColumnName("descripcio");
            entity.Property(e => e.Duracio)
                .HasColumnType("int(11)")
                .HasColumnName("duracio");
            entity.Property(e => e.IdObjArmHabActiu)
                .HasColumnType("int(11)")
                .HasColumnName("id_obj_arm_hab_actiu");
            entity.Property(e => e.IdTipusEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_tipus_efecte");
            entity.Property(e => e.Rang)
                .HasColumnType("int(11)")
                .HasColumnName("rang");
            entity.Property(e => e.TipusDany)
                .HasColumnType("int(11)")
                .HasColumnName("tipus_dany");

            entity.HasOne(d => d.IdObjArmHabActiuNavigation).WithMany(p => p.Efectes)
                .HasForeignKey(d => d.IdObjArmHabActiu)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_ibfk_2");

            entity.HasOne(d => d.IdTipusEfecteNavigation).WithMany(p => p.Efectes)
                .HasForeignKey(d => d.IdTipusEfecte)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_ibfk_1");
        });

        modelBuilder.Entity<EfecteEstat>(entity =>
        {
            entity.HasKey(e => e.IdEfecteEstat).HasName("PRIMARY");

            entity.ToTable("efecte_estat");

            entity.HasIndex(e => e.IdEfecte, "id_efecte");

            entity.HasIndex(e => e.IdEstat, "id_estat");

            entity.Property(e => e.IdEfecteEstat)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte_estat");
            entity.Property(e => e.IdEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte");
            entity.Property(e => e.IdEstat)
                .HasColumnType("int(11)")
                .HasColumnName("id_estat");

            entity.HasOne(d => d.IdEfecteNavigation).WithMany(p => p.EfecteEstats)
                .HasForeignKey(d => d.IdEfecte)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_estat_ibfk_1");

            entity.HasOne(d => d.IdEstatNavigation).WithMany(p => p.EfecteEstats)
                .HasForeignKey(d => d.IdEstat)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_estat_ibfk_2");
        });

        modelBuilder.Entity<EfecteInvocacio>(entity =>
        {
            entity.HasKey(e => e.IdEfecteInvo).HasName("PRIMARY");

            entity.ToTable("efecte_invocacio");

            entity.HasIndex(e => e.IdEfecte, "id_efecte");

            entity.HasIndex(e => e.IdPersonatge, "id_personatge");

            entity.Property(e => e.IdEfecteInvo)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte_invo");
            entity.Property(e => e.IdEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte");
            entity.Property(e => e.IdPersonatge)
                .HasColumnType("int(11)")
                .HasColumnName("id_personatge");

            entity.HasOne(d => d.IdEfecteNavigation).WithMany(p => p.EfecteInvocacios)
                .HasForeignKey(d => d.IdEfecte)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_invocacio_ibfk_1");

            entity.HasOne(d => d.IdPersonatgeNavigation).WithMany(p => p.EfecteInvocacios)
                .HasForeignKey(d => d.IdPersonatge)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_invocacio_ibfk_2");
        });

        modelBuilder.Entity<EfecteModEstadistica>(entity =>
        {
            entity.HasKey(e => e.IdEfecteMod).HasName("PRIMARY");

            entity.ToTable("efecte_mod_estadistica");

            entity.HasIndex(e => e.IdEfecte, "id_efecte");

            entity.Property(e => e.IdEfecteMod)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte_mod");
            entity.Property(e => e.IdEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_efecte");
            entity.Property(e => e.NomStat)
                .HasColumnType("int(11)")
                .HasColumnName("nom_stat");
            entity.Property(e => e.Operacio)
                .HasColumnType("int(11)")
                .HasColumnName("operacio");
            entity.Property(e => e.Valor).HasColumnName("valor");

            entity.HasOne(d => d.IdEfecteNavigation).WithMany(p => p.EfecteModEstadisticas)
                .HasForeignKey(d => d.IdEfecte)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("efecte_mod_estadistica_ibfk_1");
        });

        modelBuilder.Entity<Estat>(entity =>
        {
            entity.HasKey(e => e.IdEstat).HasName("PRIMARY");

            entity.ToTable("estat");

            entity.HasIndex(e => e.Nom, "nom").IsUnique();

            entity.Property(e => e.IdEstat)
                .HasColumnType("int(11)")
                .HasColumnName("id_estat");
            entity.Property(e => e.Icona)
                .HasMaxLength(500)
                .HasColumnName("icona");
            entity.Property(e => e.Imatge)
                .HasMaxLength(500)
                .HasColumnName("imatge");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .HasColumnName("nom");
        });

        modelBuilder.Entity<Jugador>(entity =>
        {
            entity.HasKey(e => e.IdJugador).HasName("PRIMARY");

            entity.ToTable("jugador");

            entity.HasIndex(e => e.IdPersonatge, "id_personatge");

            entity.Property(e => e.IdJugador)
                .HasColumnType("int(11)")
                .HasColumnName("id_jugador");
            entity.Property(e => e.IdPersonatge)
                .HasColumnType("int(11)")
                .HasColumnName("id_personatge");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .HasColumnName("nom");

            entity.HasOne(d => d.IdPersonatgeNavigation).WithMany(p => p.Jugadors)
                .HasForeignKey(d => d.IdPersonatge)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("jugador_ibfk_1");
        });

        modelBuilder.Entity<Personatge>(entity =>
        {
            entity.HasKey(e => e.IdPersonatge).HasName("PRIMARY");

            entity.ToTable("personatge");

            entity.Property(e => e.IdPersonatge)
                .HasColumnType("int(11)")
                .HasColumnName("id_personatge");
            entity.Property(e => e.CriticBase).HasColumnName("critic_base");
            entity.Property(e => e.CriticMultiplicadorBase).HasColumnName("critic_multiplicador_base");
            entity.Property(e => e.DanyFisicBase).HasColumnName("dany_fisic_base");
            entity.Property(e => e.DanyMagicBase).HasColumnName("dany_magic_base");
            entity.Property(e => e.DefensaFisicaBase).HasColumnName("defensa_fisica_base");
            entity.Property(e => e.DefensaMagicaBase).HasColumnName("defensa_magica_base");
            entity.Property(e => e.HpBase).HasColumnName("hp_base");
            entity.Property(e => e.Icona)
                .HasMaxLength(500)
                .HasColumnName("icona");
            entity.Property(e => e.Imatge)
                .HasMaxLength(500)
                .HasColumnName("imatge");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .HasColumnName("nom");
            entity.Property(e => e.Seleccionable).HasColumnName("seleccionable");
            entity.Property(e => e.Velocitat).HasColumnName("velocitat");
        });

        modelBuilder.Entity<PersonatgeAccio>(entity =>
        {
            entity.HasKey(e => e.IdPersonatgeAccio).HasName("PRIMARY");

            entity.ToTable("personatge_accio");

            entity.HasIndex(e => e.IdObjhabarmActiu, "id_objhabarm_actiu");

            entity.HasIndex(e => e.IdPersonatge, "id_personatge");

            entity.Property(e => e.IdPersonatgeAccio)
                .HasColumnType("int(11)")
                .HasColumnName("id_personatge_accio");
            entity.Property(e => e.Equipada).HasColumnName("equipada");
            entity.Property(e => e.IdObjhabarmActiu)
                .HasColumnType("int(11)")
                .HasColumnName("id_objhabarm_actiu");
            entity.Property(e => e.IdPersonatge)
                .HasColumnType("int(11)")
                .HasColumnName("id_personatge");

            entity.HasOne(d => d.IdObjhabarmActiuNavigation).WithMany(p => p.PersonatgeAccios)
                .HasForeignKey(d => d.IdObjhabarmActiu)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("personatge_accio_ibfk_2");

            entity.HasOne(d => d.IdPersonatgeNavigation).WithMany(p => p.PersonatgeAccios)
                .HasForeignKey(d => d.IdPersonatge)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("personatge_accio_ibfk_1");
        });

        modelBuilder.Entity<TipusEfecte>(entity =>
        {
            entity.HasKey(e => e.IdTipusEfecte).HasName("PRIMARY");

            entity.ToTable("tipus_efecte");

            entity.HasIndex(e => e.TipusEfecte1, "tipus_efecte").IsUnique();

            entity.Property(e => e.IdTipusEfecte)
                .HasColumnType("int(11)")
                .HasColumnName("id_tipus_efecte");
            entity.Property(e => e.Icona)
                .HasMaxLength(500)
                .HasColumnName("icona");
            entity.Property(e => e.Imatge)
                .HasMaxLength(500)
                .HasColumnName("imatge");
            entity.Property(e => e.TipusEfecte1)
                .HasColumnType("int(11)")
                .HasColumnName("tipus_efecte");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
