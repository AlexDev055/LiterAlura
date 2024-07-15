package com.literatura.challenge_2_back_literatura.Libreria;
import com.literatura.challenge_2_back_literatura.config.ConsumoApiGutendex;
import com.literatura.challenge_2_back_literatura.config.ConvertirDatos;
import com.literatura.challenge_2_back_literatura.models.Autor;
import com.literatura.challenge_2_back_literatura.models.Libro;
import com.literatura.challenge_2_back_literatura.models.LibrosRespuestaApi;
import com.literatura.challenge_2_back_literatura.models.records.DatosLibro;
import com.literatura.challenge_2_back_literatura.repository.iAutorRepository;
import com.literatura.challenge_2_back_literatura.repository.iLibroRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class Lib {

    private Scanner sc = new Scanner(System.in);
    private ConsumoApiGutendex consumoApi = new ConsumoApiGutendex();
    private ConvertirDatos convertir = new ConvertirDatos();
    private static String API_BASE = "https://gutendex.com/books/?search=";

    private List<Libro> datosLibro = new ArrayList<>();
    private iLibroRepository libroRepository;
    private iAutorRepository autorRepository;
    public Libreria(iLibroRepository libroRepository, iAutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void consumo(){
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    
                    |***************************************************|
                    |*****       WELCOME TO MY LIBRARY      ******|
                    |***************************************************|
                    
                    1 - Agregar Libro por su Nombre
                    2 - Libros buscados
                    3 - Buscar libro por Nombre
                    4 - Buscar todos los Autores de libros buscados
                    5 - Buscar Autores por año
                    6 - Buscar Libros por Idioma
                    7 - Top 10 Libros mas Descargados
                    8 - Buscar Autor por Nombre
                   
                    
               
                    0 - Salir
                    
                    |<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|
                    |*****            PORFAVOR INGRESA UNA OPCION         ******|
                    |>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|
                    """;

            try {
                System.out.println(menu);
                opcion = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {

                System.out.println("|****************************************|");
                System.out.println("|  ha ocurrido un error por favor ingresa un numero valido.  |");
                System.out.println("|****************************************|\n");
                sc.nextLine();
                continue;
            }



            switch (opcion){
                case 1:
                    buscarLibroEnLaWeb();
                    break;
                case 2:
                    librosBuscados();
                    break;
                case 3:
                    buscarLibroPorNombre();
                    break;
                case 4:
                    BuscarAutores();
                    break;
                case 5:
                    buscarAutoresPorAnio();
                    break;
                case 6:
                    buscarLibrosPorIdioma();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 0:
                    opcion = 0;
                    System.out.println("|<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|");
                    System.out.println("|    Nos vemos!!!|");
                    System.out.println("|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|\n");
                    break;
                default:
                    System.out.println("|<<<<<<<<<<<<<<<<<<<<<<<<|");
                    System.out.println("|  Error La Opción Es Incorrecta. |");
                    System.out.println("|>>>>>>>>>>>>>>>>>>>>>|\n");
                    System.out.println("Intente con otra  Opción");
                    consumo();
                    break;
            }
        }
    }

    private Libro getDatosLibro(){
        System.out.println("Ingrese el nombre de tu libro: ");
        var nombreDelLibro = sc.nextLine().toLowerCase();
        var json = consumoApi.obtenerDatos(API_BASE + nombreDelLibro.replace(" ", "%20"));
        
        LibrosRespuestaApi datos = convertir.convertDataJsonToJava(json, LibrosRespuestaApi.class);

            if (datos != null && datos.getResultadoLibros() != null && !datos.getResultadoLibros().isEmpty()) {
                DatosLibro primerLibro = datos.getResultadoLibros().get(0); 
            } else {
                System.out.println("No se encontraron resultados coincidentes.");
                return null;
            }
    }


    private void buscarLibroEnLaWeb() {
        Libro libro = getDatosLibro();

        if (libro == null){
            System.out.println("Libro no encontrado. el valor es nulo");
            return;
        }

        
        try{
            boolean libroExists = libroRepository.existsByTitulo(libro.getTitulo());
            if (libroExists){
                System.out.println("Ese libro ya existe en la base de datos!!!!");
            }else {
                libroRepository.save(libro);
                System.out.println(libro.toString());
            }
        }catch (InvalidDataAccessApiUsageException e){
            System.out.println("No se puede agregar el libro buscado!");
        }
    }

    @Transactional(readOnly = true)
    private void librosBuscados(){
        
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("La base de datos no encontro ningun libro.");
        } else {
            System.out.println("La base de datos encontro los siguientes libros:");
            for (Libro libro : libros) {
                System.out.println(libro.toString());
            }
        }
    }

    private void buscarLibroPorNombre() {
        System.out.println("Ingrese Titulo del libro que deseas  buscar: ");
        var titulo = sc.nextLine();
        Libro libroBuscado = libroRepository.findByTituloContainsIgnoreCase(titulo);
        if (libroBuscado != null) {
            System.out.println("El libro encontrado  fue: " + libroBuscado);
        } else {
            System.out.println("El libro '" + titulo + "' no se encontró en la base dedatos.");
        }
    }

    private  void BuscarAutores()
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("No se pudieron encontrar los  libros en la base de datos. :( \n");
        } else {
            System.out.println("Libros que se encontraron en la base de datos: \n");
            Set<String> autoresUnicos = new HashSet<>();
            for (Autor autor : autores) {
             
                if (autoresUnicos.add(autor.getNombre())){
                    System.out.println(autor.getNombre()+'\n');
                }
            }
        }
    }

    private void  buscarLibrosPorIdioma(){
        System.out.println("En cual Idioma deseas buscar: \n");
        System.out.println("|+++++++++++++++++++++++++++++++++++++++|");
        System.out.println("|  Opción - es : Libros que estan en español. |");
        System.out.println("|  Opción - en : Libros qe estan en ingles.  |");
        System.out.println("|+++++++++++++++++++++++++++++++++++++++|\n");

        var idioma = sc.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron los libros en la base de datos.");
        } else {
            System.out.println("Libros segun idioma encontrados en la base de datos:");
            for (Libro libro : librosPorIdioma) {
                System.out.println(libro.toString());
            }
        }

    }

    private void buscarAutoresPorAnio() {


        System.out.println("Indica el año para consultar qe autores se encuentran vivos en : \n");
        var anioBuscado = sc.nextInt();
        sc.nextLine();

        List<Autor> autoresVivos = autorRepository.findByCumpleaniosLessThanOrFechaFallecimientoGreaterThanEqual(anioBuscado, anioBuscado);

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores que estuvieran vivos en el año " + anioBuscado + ".");
        } else {
            System.out.println("Los autores vivian en" + anioBuscado + " son:");
            Set<String> autoresUnicos = new HashSet<>();

            for (Autor autor : autoresVivos) {
                if (autor.getCumpleanios() != null && autor.getFechaFallecimiento() != null) {
                    if (autor.getCumpleanios() <= anioBuscado && autor.getFechaFallecimiento() >= anioBuscado) {
                        if (autoresUnicos.add(autor.getNombre())) {
                            System.out.println("Autor: " + autor.getNombre());
                        }
                    }
                }
            }
        }
    }

    private void top10LibrosMasDescargados(){
        List<Libro> top10Libros = libroRepository.findTop10ByTituloByCantidadDescargas();
        if (!top10Libros.isEmpty()){
            int index = 1;
            for (Libro libro: top10Libros){
                System.out.printf("Libro %d: %s Autor: %s Descargas: %d\n",
                        index, libro.getTitulo(), libro.getAutores().getNombre(), libro.getCantidadDescargas());
                index++;
            }
        }
    }


    private void buscarAutorPorNombre() {
        System.out.println("Ingrese nombre del escritor que desea buscar: ");
        var escritor = sc.nextLine();
        Optional<Autor> escritorBuscado = autorRepository.findFirstByNombreContainsIgnoreCase(escritor);
        if (escritorBuscado != null) {
            System.out.println("\nEl escritor buscado fue: " + escritorBuscado.get().getNombre());

        } else {
            System.out.println("\nEl escritor con el titulo '" + escritor + "' no se encontró.");
        }
    }
}
