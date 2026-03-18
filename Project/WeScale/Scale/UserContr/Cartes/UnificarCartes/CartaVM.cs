using BD.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WeScale.UserContr.Cartes.UnificarCartes
{
    public class CartaVM
    {
        private object carta;

        public CartaVM(object carta)
        {
            this.carta = carta;
            Carta = carta;
        }

        public string Nom
        {
            get
            {
                if (carta is Personatge p) return p.Nom;
                if (carta is Accio a) return a.Nom;
                return "";
            }
            set
            {
                if (carta is Personatge p) p.Nom = value;
                if (carta is Accio a) a.Nom = value;
            }
        }

        public string Descripcio
        {
            get
            {
                if (carta is Accio a) return a.Descripcio;
                return "";
            }
            set
            {
                if (carta is Accio a) a.Descripcio = value;
            }
        }

        public int Id { get
            {
                if (carta is Personatge p) return p.IdPersonatge;
                if (carta is Accio a) return a.IdObjActiu;
                return 0;
            }
            set
            {
                if (carta is Personatge p) p.IdPersonatge = value;
                if (carta is Accio a) a.IdObjActiu = value;
            }
        }

        public Object Carta{ get; set; }

    }
}
