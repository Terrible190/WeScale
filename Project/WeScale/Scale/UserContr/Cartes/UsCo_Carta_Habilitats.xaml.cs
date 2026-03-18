using System.Windows;
using System.Windows.Controls;
using WeScale;
using WeScale.Model;
namespace WeScale.UserContr.Cartes
{
    public partial class UsCo_Carta_Habilitats : UserControl
    {
        public Carta CartaPropietat
        {
            get { return (Carta)GetValue(CartaPropietatProperty); }
            set { SetValue(CartaPropietatProperty, value); }
        }

        public static readonly DependencyProperty CartaPropietatProperty =
            DependencyProperty.Register(
                "CartaPropietat",
                typeof(Carta),
                typeof(UsCo_Carta_Habilitats),
                new PropertyMetadata(null)
            );

        public event EventHandler<object> EliminarClicked;
        public event EventHandler<object> EditarClicked;
        public event EventHandler<object> VisualitzarClicked;

        public UsCo_Carta_Habilitats()
        {
            InitializeComponent();
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