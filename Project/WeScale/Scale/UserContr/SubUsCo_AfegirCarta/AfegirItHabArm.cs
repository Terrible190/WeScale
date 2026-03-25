using BD.Models;
using Microsoft.EntityFrameworkCore;
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
using WeScale.UserContr.Cartes.UnificarCartes;
using WeScale.ViewModels;
using WeScale.Model;
namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    /// <summary>
    /// Lógica de interacción para AfegirPersonatge.xaml
    /// </summary>
    public partial class AfegirItHabArm : UserControl
    {
        private List<Efecte> _efectes;
         
        public AfegirItHabArm(List<Efecte> efectes)
        {
            InitializeComponent();

            _efectes = efectes;
        }

        private void BtnAfegirEfecte_Click(object sender, RoutedEventArgs e)
        {
            var cartaVM = DataContext as CartaVM;

            if (cartaVM?.Carta is Accio accio)
            {
                // 🔥 coger efectos del ViewModel (NO del context)

                var ventana = new LlistaEfectes(_efectes);

                if (ventana.ShowDialog() == true)
                {
                    var efecte = ventana.EfecteSeleccionat;

                    if (!accio.Efectes.Any(e => e.IdEfecte == efecte.IdEfecte))
                    {
                        accio.Efectes.Add(efecte);
                    }
                }
            }
        }
        private void BtnEliminarEfecte_Click(object sender, RoutedEventArgs e)
        {
            if (sender is Button btn && btn.DataContext is Efecte efecte)
            {
                if (DataContext is CartaVM vm && vm.Carta is Accio accio)
                {
                    accio.Efectes.Remove(efecte);
                }
            }
        }
    }
}
