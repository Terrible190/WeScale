using System.Windows;
using System.Windows.Controls;
using BD.Models;

namespace WeScale
{
    public class CartaTemplateSelector : DataTemplateSelector
    {
        public DataTemplate PersonatgeTemplate { get; set; }
        public DataTemplate ArmaTemplate { get; set; }
        public DataTemplate HabilitatTemplate { get; set; }
        public DataTemplate ObjecteTemplate { get; set; }

        public override DataTemplate SelectTemplate(object item, DependencyObject container)
        {
            if (item is Personatge)
                return PersonatgeTemplate;

            if (item is Accio accio)
            {
                if (accio.Tipus == 1)
                    return ArmaTemplate;

                if (accio.Tipus == 2)
                    return HabilitatTemplate;

                if (accio.Tipus == 3)
                    return ObjecteTemplate;
            }

            return base.SelectTemplate(item, container);
        }
    }
}