using BD.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using WeScale.Model;
using WeScale.UserContr;
using WeScale.UserContr.Cartes;
using WeScale.UserContr.Cartes.UnificarCartes;
using WeScale.ViewModels;
using WeScale.Model;
namespace WeScale
{
    /// <summary>
    /// Lógica de interacción para MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();

            ChkPersonatges.IsChecked = true;
            ChkArmes.IsChecked = true;
            ChkHabilitats.IsChecked = true;
            ChkObjectes.IsChecked = true;
           
            DataContext = new MainViewModel();
        }

        public void CartaEditar(object sender, object carta)
        {
            var vm = (MainViewModel)DataContext;

            var vista = new UsCo_Carta(carta, ModeCarta.Editar, vm.getContext());

            Window finestra = new Window();
            finestra.Title = "Editar Carta";
            finestra.Content = vista;
            finestra.Width = 900;
            finestra.Height = 800;
            finestra.WindowStartupLocation = WindowStartupLocation.CenterScreen;

            finestra.ShowDialog();
            vm.LoadData();

        }

        private void BtnAfegirCarta_Click(object sender, RoutedEventArgs e)
        {

            var novaCarta = new Personatge();

            var vm = (MainViewModel)DataContext;

            var vista = new UsCo_Carta(novaCarta, ModeCarta.Afegir, vm.getContext());

            Window finestra = new Window();
            finestra.Title = "Afegir Carta";
            finestra.Content = vista;
            finestra.Width = 900;
            finestra.Height = 800;
            finestra.WindowStartupLocation = WindowStartupLocation.CenterScreen;

            finestra.ShowDialog();

            vm.LoadData();
        }

        public void CartaVisualitzar(object sender, object carta)
        {
            var vm = (MainViewModel)DataContext;

            var vista = new UsCo_Carta(carta, ModeCarta.Visualitzar, vm.getContext());

            Window finestra = new Window();
            finestra.Title = "Visualitzar Carta";
            finestra.Content = vista;
            finestra.Width = 900;
            finestra.Height = 800;
            finestra.WindowStartupLocation = WindowStartupLocation.CenterScreen;

            finestra.ShowDialog();
        }

        public void CartaEliminar(object sender, object carta)
        {
            MessageBox.Show($"Eliminar: {carta}");
            var vm = (MainViewModel)DataContext;

            vm.Personatges().Remove(carta as Personatge);
            vm.Armes().Remove(carta as Accio);
                vm.Habilitats().Remove(carta as Accio);
                vm.Objectes().Remove(carta as Accio);
    
                vm.Cartes.Remove(carta);    
            vm.getContext().SaveChanges();
           


        }

        private void FiltreCanvis(object sender, RoutedEventArgs e)
        {
            AplicarFiltres();
        }
        private void BuscarCanvis(object sender, TextChangedEventArgs e)
        {
            AplicarFiltres();
        }

        private void AplicarFiltres()
        {
            var vm = DataContext as MainViewModel;
            if (vm == null) return;

            string text = TxtBuscar.Text?.ToLower() ?? "";

            vm.Cartes.Clear();

            if (ChkPersonatges.IsChecked == true)
            {
                foreach (var p in vm.Personatges)
                {
                    if (p.Nom.ToLower().Contains(text) ||
                        p.IdPersonatge.ToString().Contains(text))
                    {
                        vm.Cartes.Add(p);
                    }
                }
            }

            if (ChkArmes.IsChecked == true)
            {
                foreach (var a in vm.Armes)
                {
                    if (a.Nom.ToLower().Contains(text) ||
                        a.IdObjActiu.ToString().Contains(text))
                    {
                        vm.Cartes.Add(a);
                    }
                }
            }

            if (ChkHabilitats.IsChecked == true)
            {
                foreach (var h in vm.Habilitats)
                {
                    if (h.Nom.ToLower().Contains(text) ||
                        h.IdObjActiu.ToString().Contains(text))
                    {
                        vm.Cartes.Add(h);
                    }
                }
            }

            if (ChkObjectes.IsChecked == true)
            {
                foreach (var o in vm.Objectes)
                {
                    if (o.Nom.ToLower().Contains(text) ||
                        o.IdObjActiu.ToString().Contains(text))
                    {
                        vm.Cartes.Add(o);
                    }
                }
            }
        }

    }
}
