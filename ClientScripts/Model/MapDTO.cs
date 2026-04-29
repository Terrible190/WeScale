using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Assets.Scripts.Model
{
    [Serializable]
    public class MapDTO
    {
        public int id_mapa;
        public int pisos;
        public int nivell_base;
        public NodeDTO[] nodes;
    }
}
