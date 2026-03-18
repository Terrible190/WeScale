using BD.Models;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using WeScale.UserContr.Cartes.UnificarCartes;

namespace WeScale.UserContr
{
    /// <summary>
    /// Interaction logic for AfegirCarta.xaml
    /// </summary>
    public partial class UsCo_Carta : UserControl
    {
        public object CartaActual { get; set; }

        public ModeCarta Mode { get; set; }

        public UsCo_Carta(object carta, ModeCarta mode)
        {
            InitializeComponent();

            CartaActual = carta;
            Mode = mode;

            this.DataContext = new CartaVM(carta);

            string[] opciones = { "Personatge", "Arma", "Habilitat", "Item" };

            foreach (var opcion in opciones)
            {
                Tipus.Items.Add(opcion);
            }

            Loaded += UsCo_AfegirCarta_Loaded;
        }
  
        private void UsCo_AfegirCarta_Loaded(object sender, RoutedEventArgs e)
        {
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
                Tipus.SelectedIndex = 0;
            }

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
                    Atributs.Content =CrearUI_Personatge() ;
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirPersonatge();
                    break;

                case "Arma":
                    Atributs.Content = CrearUI_Arma();
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm();
                    break;

                case "Habilitat":
                    Atributs.Content=CrearUI_Habilitat();
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm();
                    break;

                case "Item":
                    Atributs.Content=CrearUI_Item();
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm();
                    break;

                default:
                    ContentArea.Content = null;
                    break;
            }
        }

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

        private StackPanel CrearUI_Personatge()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Carta.HpBase", "Vida"));
            panel.Children.Add(CrearAtributBinding("Carta.DanyFisicBase", "Atq Fisic"));
            panel.Children.Add(CrearAtributBinding("Carta.DefensaFisicaBase", "Def Fisica"));

            panel.Children.Add(CrearAtributBinding("Carta.DanyMagicBase", "Atq Magic"));
            panel.Children.Add(CrearAtributBinding("Carta.DefensaMagicaBase", "Def Magic"));

            return panel;
        }

        private StackPanel CrearUI_Arma()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Carta", "Dany"));
            

            return panel;
        }

        private StackPanel CrearUI_Habilitat()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Cooldown", "Cooldown"));

           
            return panel;
        }

        private StackPanel CrearUI_Item()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Usos","Usos"));
            panel.Children.Add(CrearAtributBinding("Coldown","Coldown"));

            return panel;
        }

        private StackPanel CrearAtributBinding(string nomPropietat, string labelText)
        {
            StackPanel fila = new StackPanel
            {
                Orientation = Orientation.Horizontal,
                Margin = new Thickness(5)
            };

            Label label = new Label
            {
                Content = labelText,
                Width = 80,
                Background=System.Windows.Media.Brushes.White,
            };

            TextBox txt = new TextBox
            {
                Width = 50,
                HorizontalContentAlignment = HorizontalAlignment.Center
            };

            Binding binding = new Binding(nomPropietat)
            {
                Mode = BindingMode.TwoWay,
                UpdateSourceTrigger = UpdateSourceTrigger.PropertyChanged
            };

            txt.SetBinding(TextBox.TextProperty, binding);

            Button up = new Button { Content = "▲", Width = 20 };
            Button down = new Button { Content = "▼", Width = 20 };

            up.Click += (s, e) =>
            {
                if (int.TryParse(txt.Text, out int val))
                    txt.Text = (val + 1).ToString();
            };

            down.Click += (s, e) =>
            {
                if (int.TryParse(txt.Text, out int val))
                    txt.Text = (val - 1).ToString();
            };

            fila.Children.Add(label);
            fila.Children.Add(txt);
            fila.Children.Add(up);
            fila.Children.Add(down);

            return fila;
        }

    }
}