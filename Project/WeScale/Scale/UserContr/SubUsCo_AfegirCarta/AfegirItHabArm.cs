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
using System.Collections.ObjectModel;
namespace WeScale.UserContr.SubUsCo_AfegirCarta
{
    /// <summary>
    /// Lógica de interacción para AfegirPersonatge.xaml
    /// </summary>
    public partial class AfegirItHabArm : UserControl
    {
        private List<Efecte> _efectes;
         private AppDbContext _context = new AppDbContext();
        private Object carta;
        public ObservableCollection<AccioEfecte> AccioEfectes { get; set; }
        public AfegirItHabArm(List<Efecte> efectes, AppDbContext _context, Object Carta)
        {
            InitializeComponent();

            _efectes = efectes;
            this._context = _context;
            carta = Carta;
            if (carta is Accio accio)
            {
                AccioEfectes = new ObservableCollection<AccioEfecte>(
                    accio.AccioEfectes
                );
            }

        }

        private void BtnAfegirEfecte_Click(object sender, RoutedEventArgs e)
        {
            var cartaVM = DataContext as CartaVM;

            if (cartaVM?.Carta is Accio accio)
            {
                var ventana = new LlistaEfectes(_efectes);

                if (ventana.ShowDialog() == true)
                {
                    var efecte = ventana.EfecteSeleccionat;

                    bool yaExiste = accio.AccioEfectes
                        .Any(ae => ae.IdEfecte == efecte.IdEfecte);

                    if (!yaExiste)
                    {
                        var nouAccioEfecte = new AccioEfecte
                        {
                            IdAccio = accio.IdObjActiu,
                            IdEfecte = efecte.IdEfecte,

                            IdAccioNavigation = accio,
                            IdEfecteNavigation = efecte
                        };

                        accio.AccioEfectes.Add(nouAccioEfecte);
                        AccioEfectes.Add(nouAccioEfecte);
                    }
                }
            }
        }

        private void BtnEliminarEfecte_Click(object sender, RoutedEventArgs e)
        {
            if (sender is Button btn && btn.DataContext is AccioEfecte accioEfecte)
            {
                // borrar de EF/lista lógica
                if (carta is Accio accio)
                {
                    accio.AccioEfectes.Remove(accioEfecte);
                }

                // borrar de lista visual
                AccioEfectes.Remove(accioEfecte);

                // borrar BD
                var entity = _context.AccioEfectes
                    .FirstOrDefault(x => x.IdAccioEfecte == accioEfecte.IdAccioEfecte);

                if (entity != null)
                {
                    _context.AccioEfectes.Remove(entity);
                   
                }
            }
        }
    }
}
