using BD.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Media;
using WeScale.UserContr.Cartes.UnificarCartes;
using WeScale.ViewModels;

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
        private List<Efecte> _efectes;
        public UsCo_Carta(object carta, ModeCarta mode, AppDbContext context)
        {
            InitializeComponent();
            _context = context;
            _efectes = _context.Efectes.ToList();
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
                context.PersonatgeAccios.Remove(rel); 
            }

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
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm(_efectes);
                    break;

                case "Habilitat":
                    Atributs.Content=CrearUI_Habilitat();
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm(_efectes);
                    break;

                case "Item":
                    Atributs.Content=CrearUI_Item();
                    ContentArea.Content = new SubUsCo_AfegirCarta.AfegirItHabArm(_efectes);
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

            panel.Children.Add(CrearAtributBinding("Carta.DanyFisicBase", "Dany"));

            return panel;
        }

        private StackPanel CrearUI_Habilitat()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Carta.Cooldown", "Cooldown"));

           
            return panel;
        }

        private StackPanel CrearUI_Item()
        {
            StackPanel panel = new StackPanel();

            panel.Children.Add(CrearAtributBinding("Carta.Usos","Usos"));
            panel.Children.Add(CrearAtributBinding("Carta.Coldown","Coldown"));

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
            var vm = (CartaVM)DataContext;

            // 🔥 VALIDACIÓN GLOBAL
            string error = ValidarCarta(vm.Carta);
            if (error != null)
            {
                MessageBox.Show(error);
                return;
            }

            // 🔥 DETECTAR TIPO REAL
            if (vm.Carta is Personatge p)
            {
                AfegirPersonatge(_context, p);
            }
            else if (vm.Carta is Accio a)
            {
                AfegirAccio(_context, a);
            }

            int cambios = _context.SaveChanges();
            MessageBox.Show($"Cambios: {cambios}");

            Window.GetWindow(this)?.Close();
        }

        private string ValidarCarta(object carta)
        {
            if (carta is Personatge p)
            {
                if (string.IsNullOrWhiteSpace(p.Nom))
                    return "El nom del personatge es obligatori";

                if (p.HpBase < 0)
                    return "La vida no pot ser negativa";

                if (p.DanyFisicBase < 0 || p.DanyMagicBase < 0)
                    return "El dany no pot ser negatiu";

                if (p.PersonatgeAccios == null)
                    return "Error en accions del personatge";

                return null;
            }

            if (carta is Accio a)
            {
                if (string.IsNullOrWhiteSpace(a.Nom))
                    return "El nom de l'acció es obligatori";

                if (a.Tipus < 1 || a.Tipus > 3)
                    return "Tipus d'acció invalid";

                if (a.Cooldown < 0)
                    return "Cooldown invalid";

                return null;
            }

            return "Tipus de carta desconegut";
        }


        private void AfegirAccio(AppDbContext context, Accio a)
        {
            var nova = new Accio
            {
                Nom = a.Nom,
                Tipus = a.Tipus, // 🔥 1 arma / 2 habilidad / 3 item
                Cooldown = a.Cooldown,
                Descripcio = a.Descripcio,
                Imatge = a.Imatge,
                Icona = a.Icona,
                Usos = a.Usos,
                Estadistica = a.Estadistica,
                NivellMinim = a.NivellMinim,
                Tier = a.Tier,
                Efectes = new List<Efecte>()
            };

            // 🔥 RELACIÓN EFECTES (MUY IMPORTANTE)
            if (a.Efectes != null)
            {
                foreach (var e in a.Efectes)
                {
                    // 👇 NO crear nuevos si ya existen en BD
                    var efecteBD = context.Efectes.Find(e.IdEfecte);

                    if (efecteBD != null)
                        nova.Efectes.Add(efecteBD);
                }
            }

            context.Accios.Add(nova);
        }
        private void AfegirPersonatge(AppDbContext context, Personatge p)
        {
            var nou = new Personatge
            {
                Nom = Safe(p.Nom),
                Seleccionable = p.Seleccionable,

                Imatge = Safe(p.Imatge),   // 🔥 FIX
                Icona = Safe(p.Icona),     // 🔥 FIX

                Velocitat = p.Velocitat,
                HpBase = p.HpBase,
                DanyFisicBase = p.DanyFisicBase,
                DanyMagicBase = p.DanyMagicBase,

                DefensaFisicaBase = p.DefensaFisicaBase,
                DefensaMagicaBase = p.DefensaMagicaBase,

                CriticBase = p.CriticBase,
                CriticMultiplicadorBase = p.CriticMultiplicadorBase >= 1 ? p.CriticMultiplicadorBase : 1,

                PersonatgeAccios = new List<PersonatgeAccio>()
            };

            if (p.PersonatgeAccios != null)
            {
                foreach (var pa in p.PersonatgeAccios)
                {
                    nou.PersonatgeAccios.Add(new PersonatgeAccio
                    {
                        IdObjhabarmActiu = pa.IdObjhabarmActiu,
                        Equipada = pa.Equipada
                    });
                }
            }

            context.Personatges.Add(nou);
        }

        private string Safe(string value)
        {
            return string.IsNullOrWhiteSpace(value) ? "" : value;
        }

    }
}