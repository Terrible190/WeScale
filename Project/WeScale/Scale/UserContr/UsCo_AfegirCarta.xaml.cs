using BD.Models;
using System;
using System.Windows;
using System.Windows.Controls;

namespace WeScale.UserContr
{
    /// <summary>
    /// Interaction logic for AfegirCarta.xaml
    /// </summary>
    public partial class UsCo_AfegirCarta : UserControl
    {
        public object CartaActual { get; set; }

        public ModeCarta Mode { get; set; }

        public UsCo_AfegirCarta(object carta, ModeCarta mode)
        {
            InitializeComponent();

            CartaActual = carta;
            Mode = mode;

            DataContext = carta;

            string[] opciones = { "Personatge", "Arma", "Habilitat", "Item" };

            foreach (var opcion in opciones)
            {
                Tipus.Items.Add(opcion);
            }

            Loaded += UsCo_AfegirCarta_Loaded;
        }

        private void UsCo_AfegirCarta_Loaded(object sender, RoutedEventArgs e)
        {
            // detectar tipo automaticamente si hay carta
            if (CartaActual != null)
            {
                if (CartaActual is Personatge)
                {
                    Tipus.SelectedItem = "Personatge";
                }
                else if (CartaActual is Accio accio)
                {
                    switch (accio.Tipus)
                    {
                        case 1:
                            Tipus.SelectedItem = "Arma";
                            break;

                        case 2:
                            Tipus.SelectedItem = "Habilitat";
                            break;

                        case 3:
                            Tipus.SelectedItem = "Item";
                            break;
                    }
                }
            }
            else
            {
                // si es afegir sin carta
                Tipus.SelectedIndex = 0;
            }

            // modo visualizacion
            if (Mode == ModeCarta.Visualitzar)
            {
                BloquearControles(this);
            }
        }

        private void Tipus_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ComboBox combo = sender as ComboBox;

            if (combo.SelectedItem == null)
                return;

            string valor = combo.SelectedItem as string;

            switch (valor)
            {
                case "Personatge":
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirPersonatge();
                    break;

                case "Arma":
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirArma();
                    break;

                case "Habilitat":
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirHabilitat();
                    break;

                case "Item":
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItem();
                    break;

                default:
                    ContentArea.Content = null;
                    break;
            }
        }

        // bloquea todos los controles en modo visualizar
        private void BloquearControles(DependencyObject parent)
        {
            foreach (var child in LogicalTreeHelper.GetChildren(parent))
            {
                if (child is TextBox tb)
                    tb.IsReadOnly = true;

                if (child is ComboBox cb)
                    cb.IsEnabled = false;

                if (child is Button btn)
                    btn.IsEnabled = false;

                if (child is DependencyObject dep)
                    BloquearControles(dep);
            }
        }
    }
}