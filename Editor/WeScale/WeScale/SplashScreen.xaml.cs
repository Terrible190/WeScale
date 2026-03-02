using System;
using System.Threading.Tasks;
using System.Windows;
using Model;
using Model.Model;
namespace WeScale
{
    public partial class SplashScreen : Window
    {
        public SplashScreen()
        {
            InitializeComponent();
            Loaded += SplashScreen_Loaded;
        }

        private async void SplashScreen_Loaded(object sender, RoutedEventArgs e)
        {
            await LoadApplicationAsync();

            MainWindow main = new MainWindow();
            main.Show();
            this.Close();
        }

        private async Task LoadApplicationAsync()
        {
            // Simulamos pasos reales
            await UpdateProgress(10);

            await Task.Delay(300); // Inicializar config
            await UpdateProgress(30);

            await LoadFromDatabaseAsync();
            await UpdateProgress(80);

            await Task.Delay(300); // Finalizar
            await UpdateProgress(100);
        }

        private async Task LoadFromDatabaseAsync()
        {
            using (var context = new AppDbContext())
            {
                //var data = await context.YourTable.ToListAsync();
                // Aquí podrías guardar en memoria global si quieres
            }
        }

        private async Task UpdateProgress(int value)
        {
            LoadingBar.Value = value;
            await Task.Delay(200);
        }
    }
}