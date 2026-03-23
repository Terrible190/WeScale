using BD.Models;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using WeScale.ViewModels;

namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    public partial class LlistaAccions : Window
    {
        private List<Accio> _totes;

        public ObservableCollection<Accio> AccionsFiltrades { get; set; }

        public Accio AccioSeleccionada { get; set; }

        public LlistaAccions(MainViewModel vm)
        {
            InitializeComponent();

            // 🔥 juntar todas las acciones
            _totes = vm.Armes
                .Concat(vm.Habilitats)
                .Concat(vm.Objectes)
                .ToList();

            AccionsFiltrades = new ObservableCollection<Accio>(_totes);

            DataContext = this;
        }

        // =========================
        // FILTRAR POR TIPO
        // =========================
        public void Filtrar(int tipus)
        {
            var filtradas = _totes.Where(a => a.Tipus == tipus).ToList();

            AccionsFiltrades.Clear();

            foreach (var a in filtradas)
                AccionsFiltrades.Add(a);
        }

        // =========================
        // BUSCADOR
        // =========================
        private void TxtBuscar_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {
            string text = TxtBuscar.Text.ToLower();

            var filtradas = _totes.Where(a =>
                a.Nom.ToLower().Contains(text)).ToList();

            AccionsFiltrades.Clear();

            foreach (var a in filtradas)
                AccionsFiltrades.Add(a);
        }

        // =========================
        // DOBLE CLICK (PRO)
        // =========================
        private void LvAccions_DoubleClick(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            Seleccionar();
        }

        // =========================
        // BOTÓN
        // =========================
        private void BtnSeleccionar_Click(object sender, RoutedEventArgs e)
        {
            Seleccionar();
        }

        // =========================
        // SELECCIONAR
        // =========================
        private void Seleccionar()
        {
            if (LvAccions.SelectedItem is Accio accio)
            {
                AccioSeleccionada = accio;
                DialogResult = true;
            }
        }
    }
}