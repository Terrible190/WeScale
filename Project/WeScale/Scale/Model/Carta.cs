using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WeScale.Model
{
    public class Carta
    {
        public int Id { get; set; }

        public string Nom { get; set; }

        public string Tipus { get; set; }

        public string Imatge { get; set; }

        public object Dades { get; set; }
    }
}
