using BD.Models;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    public partial class LlistaEfectes : Window
    {
        private List<Efecte> _totsEfectes;

        public ObservableCollection<Efecte> EfectesFiltrats { get; set; }

        public Efecte EfecteSeleccionat { get; private set; }

        public LlistaEfectes(List<Efecte> efectes)
        {
            InitializeComponent();

            _totsEfectes = efectes;
            EfectesFiltrats = new ObservableCollection<Efecte>(efectes);
            DataContext = this;
        }


        private void ListaEfectes_DoubleClick(object sender, MouseButtonEventArgs e)
        {
            if (ListaEfectes.SelectedItem is Efecte efecte)
            {
                EfecteSeleccionat = efecte;
                DialogResult = true;
                Close();
            }
        }

        private void BtnSeleccionar_Click(object sender, RoutedEventArgs e)
        {
            if (ListaEfectes.SelectedItem is Efecte efecte)
            {
                EfecteSeleccionat = efecte;
                DialogResult = true;
                Close();
            }
        }
    }
}