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

namespace WeScale.UserContr.Cartes
{
    /// <summary>
    /// Interaction logic for UsCo_Carta_Personatge.xaml
    /// </summary>
    public partial class UsCo_Carta_Personatge : UserControl
    {
        public UsCo_Carta_Personatge()
        {
            InitializeComponent();
        }


        public Carta CartaPropietat
        {
            get { return (Carta)GetValue(CartaPropietatProperty); }
            set { SetValue(CartaPropietatProperty, value); }
        }

        public static readonly DependencyProperty CartaPropietatProperty =
            DependencyProperty.Register(
                "CartaPropietat",
                typeof(Carta),
                typeof(UsCo_Carta_Personatge),
                new PropertyMetadata(null)
            );

        public event EventHandler<object> EliminarClicked;
        public event EventHandler<object> EditarClicked;
        public event EventHandler<object> VisualitzarClicked;


        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {
            var window = Application.Current.MainWindow as MainWindow;

            if (window != null)
            {
                EditarClicked += window.CartaEditar;
                VisualitzarClicked += window.CartaVisualitzar;
                EliminarClicked += window.CartaEliminar;
            }
        }

        private void BtnVisualitzar_Click(object sender, RoutedEventArgs e)
        {
            VisualitzarClicked?.Invoke(this, DataContext);
        }

        private void BtnEditar_Click(object sender, RoutedEventArgs e)
        {
            EditarClicked?.Invoke(this, DataContext);
        }

        private void BtnEliminar_Click(object sender, RoutedEventArgs e)
        {
            EliminarClicked?.Invoke(this, DataContext);
        }
    }
}