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

namespace WeScale.UserContr
{
    /// <summary>
    /// Interaction logic for AfegirCarta.xaml
    /// </summary>
    public partial class UsCo_AfegirCarta : UserControl
    {
        public UsCo_AfegirCarta()
        {
            InitializeComponent();


            string[] opciones = { "Personatge", "Arma", "Habilitat", "Item" };
            foreach (var opcion in opciones)
            {
                Tipus.Items.Add(opcion);
            }

            if (Tipus.Items.Count > 0)
            {
                Tipus.SelectedIndex = 1;
            }


        }

        private void Tipus_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ComboBox combo = sender as ComboBox;

            if (combo.SelectedItem != null)
            {
                string valor = combo.SelectedItem.ToString();

                switch (valor)
                {
                    case "Personatge":
                        ContentArea.Content = new SubUsCo_AfegirCarta.AfegirPersonatge();
                        break;

                    case "Arma":
                        ContentArea.Content = null;
                        break;

                    case "Habilitat":
                        ContentArea.Content = null;
                        break;

                    case "Item":
                        ContentArea.Content = null;
                        break;

                    default:
                        ContentArea.Content = null;
                        break;
                }
            }
        }
    }
}
