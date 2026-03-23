using BD.Models;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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

namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    /// <summary>
    /// Interaction logic for InventariPersonatges.xaml
    /// </summary>
    public partial class InventariPersonatges : UserControl
    {
        public ObservableCollection<Accio> Habilitats { get; set; }
        public ObservableCollection<Accio> Armes { get; set; }
        public ObservableCollection<Accio> Objectes { get; set; }

        public InventariPersonatges(Personatge p)
        {
            InitializeComponent();

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

            this.DataContext = this;
        }
    }
}
