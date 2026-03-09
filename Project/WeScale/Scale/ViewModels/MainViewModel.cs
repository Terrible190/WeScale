using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using WeScale.UserContr;
using BD.Models;

namespace WeScale.ViewModels
{
    public class MainViewModel
    {
        private readonly AppDbContext _context;

        public ObservableCollection<object> Cartes { get; set; }

        public ObservableCollection<Personatge> Personatges { get; set; }

        public ObservableCollection<Accio> Armes { get; set; }
        public ObservableCollection<Accio> Habilitats { get; set; }
        public ObservableCollection<Accio> Objectes { get; set; }

        public MainViewModel()
        {
            _context = new AppDbContext();

           LoadData();

        }

        public void LoadData()
        {
            // Personatges
            Personatges = new ObservableCollection<Personatge>(
                _context.Personatges.ToList()
            );

            // Accions
            var accions = _context.Accios.ToList();

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
            {
                Cartes.Add(personatge);
            }

            foreach (var arma in Armes)
            {
                Cartes.Add(arma);
            }

            foreach (var habilitat in Habilitats)
            {
                Cartes.Add(habilitat);
            }

            foreach (var obj in Objectes)
            {
                Cartes.Add(obj);

            }
        }
    }

}