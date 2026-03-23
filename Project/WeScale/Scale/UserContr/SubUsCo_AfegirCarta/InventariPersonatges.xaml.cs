using BD.Models;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using WeScale.ViewModels;

namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    public partial class InventariPersonatges : UserControl
    {
        private Personatge _personatge;

        public ObservableCollection<Accio> Habilitats { get; set; }
        public ObservableCollection<Accio> Armes { get; set; }
        public ObservableCollection<Accio> Objectes { get; set; }

        public InventariPersonatges(Personatge p)
        {
            InitializeComponent();

            _personatge = p;

            // 🔥 CARGAR DESDE BD
            Habilitats = new ObservableCollection<Accio>(
                p.PersonatgeAccios
                 .Where(pa => pa.IdObjhabarmActiuNavigation.Tipus == 2)
                 .Select(pa => pa.IdObjhabarmActiuNavigation)
            );

            Armes = new ObservableCollection<Accio>(
                p.PersonatgeAccios
                 .Where(pa => pa.IdObjhabarmActiuNavigation.Tipus == 1)
                 .Select(pa => pa.IdObjhabarmActiuNavigation)
            );

            Objectes = new ObservableCollection<Accio>(
                p.PersonatgeAccios
                 .Where(pa => pa.IdObjhabarmActiuNavigation.Tipus == 3)
                 .Select(pa => pa.IdObjhabarmActiuNavigation)
            );

            DataContext = this;
        }

        // =========================
        // ➕ AFEGIR HABILITAT
        // =========================
        private void BtnAfegirHabilitat_Click(object sender, RoutedEventArgs e)
        {
            ObrirSelector(2);
        }

        // =========================
        // ➕ AFEGIR ARMA
        // =========================
        private void BtnAfegirArma_Click(object sender, RoutedEventArgs e)
        {
            ObrirSelector(1);
        }

        // =========================
        // ➕ AFEGIR OBJECTE
        // =========================
        private void BtnAfegirObjecte_Click(object sender, RoutedEventArgs e)
        {
            ObrirSelector(3);
        }

        // =========================
        // 🔥 SELECTOR GENERAL
        // =========================
        private void ObrirSelector(int tipus)
        {
            var mainVM = ((MainWindow)Application.Current.MainWindow).DataContext as MainViewModel;

            var win = new LlistaAccions(mainVM);

            win.Filtrar(tipus);

            if (win.ShowDialog() == true)
            {
                AfegirAccio(win.AccioSeleccionada);
            }
        }

        // =========================
        // ➕ AFEGIR ACCIÓ
        // =========================
        public void AfegirAccio(Accio accio)
        {
            // ❗ evitar duplicados
            if (_personatge.PersonatgeAccios
                .Any(pa => pa.IdObjhabarmActiu == accio.IdObjActiu))
            {
                MessageBox.Show("Aquesta acció ja està afegida!");
                return;
            }

            // 🔥 BD (REAL)
            _personatge.PersonatgeAccios.Add(new PersonatgeAccio
            {
                IdObjhabarmActiu = accio.IdObjActiu,
                IdObjhabarmActiuNavigation = accio
            });

            // 🔥 UI
            switch (accio.Tipus)
            {
                case 1: Armes.Add(accio); break;
                case 2: Habilitats.Add(accio); break;
                case 3: Objectes.Add(accio); break;
            }
        }

        // =========================
        // ❌ ELIMINAR (BOTÓN)
        // =========================
        private void BtnEliminar_Click(object sender, RoutedEventArgs e)
        {
            if ((sender as Button)?.Tag is Accio accio)
            {
                EliminarAccio(accio);
            }
        }

        // =========================
        // ❌ ELIMINAR ACCIÓ
        // =========================
        public void EliminarAccio(Accio accio)
        {
            var rel = _personatge.PersonatgeAccios
                .FirstOrDefault(pa => pa.IdObjhabarmActiu == accio.IdObjActiu);

            if (rel != null)
            {
                _personatge.PersonatgeAccios.Remove(rel);

                // 🔥 IMPORTANTE: eliminar del contexto
                var context = ((MainWindow)Application.Current.MainWindow)
                    .DataContext as MainViewModel;

                context?.getContext().PersonatgeAccios.Remove(rel);
            }

            Armes.Remove(accio);
            Habilitats.Remove(accio);
            Objectes.Remove(accio);
        }
    }
}