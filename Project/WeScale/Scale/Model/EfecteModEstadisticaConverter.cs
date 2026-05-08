using System;
using System.Globalization;
using System.Windows.Data;
using BD.Models;

namespace WeScale.Model.Converter
{

    public class EfecteModEstadisticaConverter : IValueConverter
    {

        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var e = value as EfecteModEstadistica;
            if (e == null) return "";

            string stat = e.NomStat switch
            {
                1 => "HP",
                2 => "Dany físic",
                3 => "Defensa física",
                4 => "Defensa màgica",
                5 => "Dany màgic",
                6 => "Velocitat",
                _ => "???"
            };

            return e.Operacio switch
            {
                1 => $"{stat} +{e.Valor}",
                2 => $"{stat} x{e.Valor}",
                3 => $"{stat} = {e.Valor}",
                4 => $"{stat} {e.Valor}% actual",
                5 => $"{stat} {e.Valor}% base",
                6 => $"{stat} {e.Valor}% dany fet",
                7 => $"{stat} {e.Valor}% dany rebut",
                8 => $"{stat} /{e.Valor}",
                9 => $"{stat} mín {e.Valor}",
                10 => $"{stat} màx {e.Valor}",
                11 => $"{stat} {e.Valor}% vida màx",
                _ => $"{stat} ? {e.Valor}"
            };
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}