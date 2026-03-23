using BD.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Media;
using WeScale.UserContr.Cartes.UnificarCartes;

namespace WeScale.UserContr
{
    /// <summary>
    /// Interaction logic for AfegirCarta.xaml
    /// </summary>
    public partial class UsCo_Carta : UserControl
    {

        private AppDbContext _context;
        public object CartaActual { get; set; }

        public ModeCarta Mode { get; set; }

        public UsCo_Carta(object carta, ModeCarta mode, AppDbContext context)
        {
            InitializeComponent();
            _context = context;

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

        private void BtnGuardar_Click(object sender, RoutedEventArgs e)
        {
            switch (Mode)
            {
                case ModeCarta.Afegir:
                    GuardarNou();
                    break;

                case ModeCarta.Editar:
                    GuardarEdicio();
                    break;

                case ModeCarta.Visualitzar:
                    MessageBox.Show("No es pot guardar en mode visualització");
                    break;
            }
        }
        private void GuardarNou()
        {
            var vm = (CartaVM)DataContext;

            if (vm.Carta is Personatge p)
                AfegirPersonatge(_context, p);

            int cambios = _context.SaveChanges();
            MessageBox.Show($"Cambios: {cambios}");
            Window.GetWindow(this)?.Close();
        }

        private void GuardarEdicio()
        {
            var vm = (CartaVM)DataContext;

            if (vm.Carta is Personatge p)
                EditarPersonatge(_context, p);
            int cambios = _context.SaveChanges();

            MessageBox.Show($"Cambios guardados: {cambios}");
            Window.GetWindow(this)?.Close();
        }
        private void AfegirPersonatge(AppDbContext context, Personatge p)
        {
            var nou = new Personatge
            {
                Nom = p.Nom,
                Seleccionable = p.Seleccionable,
                Imatge = p.Imatge,
                Icona = p.Icona,
                Velocitat = p.Velocitat,
                HpBase = p.HpBase,
                DanyFisicBase = p.DanyFisicBase,
                DanyMagicBase = p.DanyMagicBase,
                DefensaFisicaBase = p.DefensaFisicaBase,
                DefensaMagicaBase = p.DefensaMagicaBase,
                CriticBase = p.CriticBase,
                CriticMultiplicadorBase = p.CriticMultiplicadorBase,
                PersonatgeAccios = new List<PersonatgeAccio>()
            };

            // 🔥 relaciones
            foreach (var pa in p.PersonatgeAccios)
            {
                nou.PersonatgeAccios.Add(new PersonatgeAccio
                {
                    IdObjhabarmActiu = pa.IdObjhabarmActiu
                });
            }

            _context.Personatges.Add(nou);
        }


        private void EditarPersonatge(AppDbContext context, Personatge p)
        {
            var original = context.Personatges
                .Include(x => x.PersonatgeAccios)
                .First(x => x.IdPersonatge == p.IdPersonatge);

            // 🔹 PROPIEDADES
            original.Nom = p.Nom;
            original.Seleccionable = p.Seleccionable;
            original.Imatge = p.Imatge;
            original.Icona = p.Icona;
            original.Velocitat = p.Velocitat;

            original.HpBase = p.HpBase;
            original.DanyFisicBase = p.DanyFisicBase;
            original.DanyMagicBase = p.DanyMagicBase;

            original.DefensaFisicaBase = p.DefensaFisicaBase;
            original.DefensaMagicaBase = p.DefensaMagicaBase;

            original.CriticBase = p.CriticBase;
            original.CriticMultiplicadorBase = p.CriticMultiplicadorBase;

            // =========================
            // 🔥 SINCRONIZAR RELACIONES
            // =========================

            var idsNous = p.PersonatgeAccios
                .Select(x => x.IdObjhabarmActiu)
                .ToList();

            // ❌ eliminar los que ya no están
            var aEliminar = original.PersonatgeAccios
                .Where(x => !idsNous.Contains(x.IdObjhabarmActiu))
                .ToList();

            foreach (var rel in aEliminar)
            {
                context.PersonatgeAccios.Remove(rel); // 🔥 CLAVE
            }

            // ➕ añadir nuevos
            var idsActuals = original.PersonatgeAccios
                .Select(x => x.IdObjhabarmActiu)
                .ToList();

            var aAfegir = idsNous
                .Where(id => !idsActuals.Contains(id))
                .ToList();

            foreach (var id in aAfegir)
            {
                original.PersonatgeAccios.Add(new PersonatgeAccio
                {
                    IdObjhabarmActiu = id,
                    IdPersonatge = original.IdPersonatge
                });
            }
        }


        private void EditarAccio(AppDbContext context, Accio a)
        {
            var original = context.Accios
                .Include(x => x.Efectes)
                .First(x => x.IdObjActiu == a.IdObjActiu);

            original.Nom = a.Nom;
            original.Tipus = a.Tipus;

            original.Efectes.Clear();

            foreach (var e in a.Efectes)
            {
                original.Efectes.Add(new Efecte
                {
                    // copia básica (ajusta según modelo)
                });
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
                    var vm = (CartaVM)DataContext;

                    Atributs.Content =CrearUI_Personatge() ;
                    ContentArea.Content = new SubUsCo_AfegirCarta.InventariPersonatges((Personatge)vm.Carta);
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

                // 🔥 CASO ESPECIAL: LISTVIEW
                if (child is ListView lv)
                {
                    foreach (var item in lv.Items)
                    {
                        var container = lv.ItemContainerGenerator.ContainerFromItem(item) as ListViewItem;
                        if (container != null)
                        {
                            BloquearVisualTree(container);
                        }
                    }
                }

                if (child is DependencyObject dep)
                    BloquearControles(dep);
            }
        }

        private void BloquearVisualTree(DependencyObject parent)
        {
            int count = VisualTreeHelper.GetChildrenCount(parent);

            for (int i = 0; i < count; i++)
            {
                var child = VisualTreeHelper.GetChild(parent, i);

                if (child is Button btn)
                    btn.IsEnabled = false;

                BloquearVisualTree(child);
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
                Style = (Style)Application.Current.Resources["RPGLabelStyle"], 
            };

            TextBox txt = new TextBox
            {
                Width = 50,
                HorizontalContentAlignment = HorizontalAlignment.Center,
                Style = (Style)Application.Current.Resources["RPGTextBoxStyle"], 
            };

            Binding binding = new Binding(nomPropietat)
            {
                Mode = BindingMode.TwoWay,
                UpdateSourceTrigger = UpdateSourceTrigger.PropertyChanged
            };

            txt.SetBinding(TextBox.TextProperty, binding);

            Button up = new Button
            {
                Content = "▲",
                Width = 20,
                Style = (Style)Application.Current.Resources["RPGButtonStyle"],
            };

            Button down = new Button
            {
                Content = "▼",
                Width = 20,
                Style = (Style)Application.Current.Resources["RPGButtonStyle"],
            };

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

        private void BtnAfegir_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}