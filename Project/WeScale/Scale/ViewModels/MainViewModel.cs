using System.Collections.ObjectModel;
using System.Linq;
using BD.Models;
using Microsoft.EntityFrameworkCore;

namespace WeScale.ViewModels
{
    public class MainViewModel
    {
        private readonly AppDbContext _context;

        public ObservableCollection<object> Cartes { get; set; }

        public int finestraCartes { get; set; } = 0;

        public ObservableCollection<Personatge> Personatges { get; set; }

        public ObservableCollection<Accio> Armes { get; set; }
        public ObservableCollection<Accio> Habilitats { get; set; }
        public ObservableCollection<Accio> Objectes { get; set; }

        public List<Accio> Accions =>
            Armes.Concat(Habilitats).Concat(Objectes).ToList();

        public AppDbContext getContext() { 
            return _context;
        }

        public MainViewModel()
        {
            _context = new AppDbContext();

            Cartes = new ObservableCollection<object>(); // ← faltava això

            LoadData();
        }

        public void LoadData()
        {
            // Personatges
            Personatges = new ObservableCollection<Personatge>(
    _context.Personatges
        .Include(p => p.PersonatgeAccios)
            .ThenInclude(pa => pa.IdObjhabarmActiuNavigation)
                .ThenInclude(a => a.Efectes)
                    .ThenInclude(e => e.EfecteEstats)
                        .ThenInclude(ee => ee.IdEstatNavigation)

        .Include(p => p.PersonatgeAccios)
            .ThenInclude(pa => pa.IdObjhabarmActiuNavigation)
                .ThenInclude(a => a.Efectes)
                    .ThenInclude(e => e.EfecteModEstadisticas)

        .ToList()
            );

            // Accions
            var accions = _context.Accios
                .Include(a => a.Efectes)
                    .ThenInclude(e => e.EfecteEstats)
                    .ThenInclude(ee => ee.IdEstatNavigation)
                .Include(a => a.Efectes)
                    .ThenInclude(e => e.EfecteModEstadisticas)
                .ToList();

            Armes = new ObservableCollection<Accio>(
                accions.Where(a => a.Tipus == 1)
            );

            Habilitats = new ObservableCollection<Accio>(
                accions.Where(a => a.Tipus == 2)
            );

            Objectes = new ObservableCollection<Accio>(
                accions.Where(a => a.Tipus == 3)
            );

            foreach (var personatge in Personatges)
                Cartes.Add(personatge);

            foreach (var arma in Armes)
                Cartes.Add(arma);

            foreach (var habilitat in Habilitats)
                Cartes.Add(habilitat);

            foreach (var obj in Objectes)
                Cartes.Add(obj);
        }
    }
}