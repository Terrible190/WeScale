using BD.Models;
using System.Windows;
using System.Windows.Controls;
using WeScale.Model;
using WeScale.ViewModels;

namespace WeScale.UserContr.Cartes
{
    public partial class UsCo_Carta_Arma : UserControl
    {
        public Carta CartaPropietat
        {
            get { return (Carta)GetValue(CartaPropietatProperty); }
            set { SetValue(CartaPropietatProperty, value); }
        }
      
      
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

      
        public static readonly DependencyProperty CartaPropietatProperty =
            DependencyProperty.Register(
                "CartaPropietat",
                typeof(Carta),
                typeof(UsCo_Carta_Arma),
                new PropertyMetadata(null)
            );

        public UsCo_Carta_Arma()
        {
            InitializeComponent();
        }

        // EVENTOS
        public event EventHandler<object> EliminarClicked;
        public event EventHandler<object> EditarClicked;
        public event EventHandler<object> VisualitzarClicked;


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