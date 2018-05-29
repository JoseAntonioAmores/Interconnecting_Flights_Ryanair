package com.rynair.interconnections;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ryanair.connections.connections.CaracteristicasVuelos;
import com.ryanair.connections.connections.ConexionesVuelos;
import com.ryanair.connections.connections.FechasVuelos;
import com.ryanair.connections.connections.Flight;
import com.ryanair.connections.connections.Ruta;

/**
 * Handles requests for the application home page.
 */
@Controller
public class MainController {
    private static final String URL_SCHEDULES_PREFIX = "https://api.ryanair.com/timetable/3/schedules/";
    private static final String URL_RUTA = "https://api.ryanair.com/core/3/routes/";

    @RequestMapping(value = "/*", method = RequestMethod.GET)
    public String wellcomeController()
    {
        return "wellcome";
    }

    @RequestMapping(value = "/interconnections", method = RequestMethod.GET)
    public @ResponseBody List<ConexionesVuelos> conParametro(@RequestParam String departure, 
            @RequestParam String arrival, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm") Date departureDateTime,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm") Date arrivalDateTime) throws RestClientException, IOException, URISyntaxException
    {
        List<ConexionesVuelos> conexionesVuelos = new ArrayList<ConexionesVuelos>();
        if((departureDateTime.getDay() == arrivalDateTime.getDay()) && (departureDateTime.getMonth() == arrivalDateTime.getMonth()) 
                && (departureDateTime.getYear() == arrivalDateTime.getYear()))
        {
            List<String> vuelosInterconectados = new ArrayList<String>();
            int añoSalida = departureDateTime.getYear()+1900;
            int mesSalida = departureDateTime.getMonth()+1;

            RestTemplate restTemplate2 =  new RestTemplate();
            Ruta[] ruta = restTemplate2.getForObject(new URI(URL_RUTA), Ruta[].class);
            boolean esConexion = checkIfinterconnectedFlight(ruta, departure, arrival);

            if(esConexion)
            {
                //VUELOS SIN VUELOS DIRECTOS
                String interconnectedFlight = null;
                System.out.println("no es vuelo directo");
                interconnectedFlight = getInterconnectedFlight(ruta, departure, arrival);
                if(interconnectedFlight != "") 
                {
                    List<ConexionesVuelos> listaConexionesNoDirecto = new ArrayList<ConexionesVuelos>();
                    listaConexionesNoDirecto = searchVuelosDeConexion(departure, interconnectedFlight, arrival, departureDateTime, arrivalDateTime);
                    for(ConexionesVuelos vuelosConexion : listaConexionesNoDirecto)
                    {
                        conexionesVuelos.add(vuelosConexion);
                    }
                }
                
            }else
            {
                //VUELOS DIRECTOS

                String urlVueloDirecto = URL_SCHEDULES_PREFIX+departure+"/"+arrival+"/years/"+añoSalida+"/months/"+mesSalida;
                RestTemplate restTemplate =  new RestTemplate();
                if(checkIfUrlIsCorrect(urlVueloDirecto)) 
                {
                    FechasVuelos fechasVueloIda = restTemplate.getForObject(new URI(urlVueloDirecto), FechasVuelos.class);

                    List<Flight> testNuevosHorarios = new ArrayList<Flight>();
                    testNuevosHorarios = obtenerHorariosVuelos(fechasVueloIda, departureDateTime, arrivalDateTime);

                    ConexionesVuelos nuevaConexion = null;
                    List<CaracteristicasVuelos> caracteristicasVuelos = new ArrayList<CaracteristicasVuelos>();
                    for(Flight vuelo : testNuevosHorarios)
                    {
                        CaracteristicasVuelos legs = new CaracteristicasVuelos(departure, arrival, horariosFinales(vuelo.getDepartureTime(), arrivalDateTime),horariosFinales(vuelo.getArrivalTime(),arrivalDateTime));
                        caracteristicasVuelos.add(legs);
                    }
                    nuevaConexion = new ConexionesVuelos(0, caracteristicasVuelos);
                    conexionesVuelos.add(nuevaConexion);
                }
                //VUELOS CON CONEXION

                vuelosInterconectados = searchConnectedFlights(ruta, departure, arrival);

                for(String vueloIntermedio : vuelosInterconectados)
                {
                    List<ConexionesVuelos> listaConexiones = new ArrayList<ConexionesVuelos>();
                    listaConexiones = searchVuelosDeConexion(departure, vueloIntermedio, arrival, departureDateTime, arrivalDateTime);
                    for(ConexionesVuelos vuelosConexion : listaConexiones)
                    {
                        conexionesVuelos.add(vuelosConexion);
                    }
                }
            }
        }
        return conexionesVuelos;
    }

    private List<ConexionesVuelos> searchVuelosDeConexion(String departure, String vueloIntermedio, String arrival, Date departureDateTime, Date arrivalDateTime) throws RestClientException, IOException, URISyntaxException
    {
        List<ConexionesVuelos> listaDeConexiones = new ArrayList<ConexionesVuelos>();

        int añoSalida = departureDateTime.getYear()+1900;
        int mesSalida = departureDateTime.getMonth()+1;

        String urlSalida = URL_SCHEDULES_PREFIX+departure+"/"+vueloIntermedio+"/years/"+añoSalida+"/months/"+mesSalida;
        String urlSalidaSegundaConexion = URL_SCHEDULES_PREFIX+vueloIntermedio+"/"+arrival+"/years/"+añoSalida+"/months/"+mesSalida;
        RestTemplate restTemplate3 =  new RestTemplate();
        FechasVuelos fechasVueloIdaPrimeraConexion = null;

        if(checkIfUrlIsCorrect(urlSalida) && checkIfUrlIsCorrect(urlSalidaSegundaConexion)) 
        {
            fechasVueloIdaPrimeraConexion = restTemplate3.getForObject(new URI(urlSalida), FechasVuelos.class);
            List<Flight> horariosConexionesSalidas = new ArrayList<Flight>();
            horariosConexionesSalidas = obtenerHorariosVuelos(fechasVueloIdaPrimeraConexion, departureDateTime, arrivalDateTime);

            RestTemplate restTemplate4 =  new RestTemplate();
            FechasVuelos fechasVueloIdaSegundaConexion = null;
            fechasVueloIdaSegundaConexion = restTemplate4.getForObject(new URI(urlSalidaSegundaConexion), FechasVuelos.class);

            //NUEVO METODO QUE DEVUELVE LAS FECHAS A PARES
            List<Flight> horariosConexionesLlegadas = new ArrayList<Flight>();
            horariosConexionesLlegadas = obtenerHorariosVuelos(fechasVueloIdaSegundaConexion, departureDateTime, arrivalDateTime);
            //FIN DEL METODO

            for(Flight vueloPrimeraConexion : horariosConexionesSalidas)
            {
                List<CaracteristicasVuelos> caracteristicasVuelosConConexion = new ArrayList<CaracteristicasVuelos>();

                CaracteristicasVuelos legsPrimeraConexion = new CaracteristicasVuelos(departure, vueloIntermedio, horariosFinales(vueloPrimeraConexion.getDepartureTime(), departureDateTime),horariosFinales(vueloPrimeraConexion.getArrivalTime(), arrivalDateTime));

                for(Flight vueloSegundaConexion : horariosConexionesLlegadas)
                {
                    if(checkhoraSegundaConexion(vueloPrimeraConexion.getArrivalTime(), vueloSegundaConexion.getDepartureTime()))
                    {
                        CaracteristicasVuelos legsSegundaConexion = new CaracteristicasVuelos(vueloIntermedio, arrival, horariosFinales(vueloSegundaConexion.getDepartureTime(), departureDateTime),horariosFinales(vueloSegundaConexion.getArrivalTime(), arrivalDateTime));

                        caracteristicasVuelosConConexion.add(legsPrimeraConexion);
                        caracteristicasVuelosConConexion.add(legsSegundaConexion);
                        ConexionesVuelos primeraConexion = new ConexionesVuelos(1, caracteristicasVuelosConConexion);
                        listaDeConexiones.add(primeraConexion);
                        break;
                    }
                }
            }
        }
        
        return listaDeConexiones;
    }

    private boolean checkhoraSegundaConexion(String llegadaPrimeraConexion, String SalidaSegundaConexion){
        boolean correctTime = false;
        int horaLlegadaPrimeraConexion = Integer.parseInt(llegadaPrimeraConexion.split(":")[0]);
        int minutosLlegadaPrimeraConexion = Integer.parseInt(llegadaPrimeraConexion.split(":")[1]);        

        int horaSalidaSegundaConexion = Integer.parseInt(SalidaSegundaConexion.split(":")[0]);
        int minutosSalidaSegundaConexion = Integer.parseInt(SalidaSegundaConexion.split(":")[1]);

        int minutosTotalesSegundaConexion = horaSalidaSegundaConexion *60 + minutosSalidaSegundaConexion;
        int minutosTotalesPrimeraConexion = horaLlegadaPrimeraConexion*60+minutosLlegadaPrimeraConexion;
        int diferencia = minutosTotalesSegundaConexion - minutosTotalesPrimeraConexion;
        if(diferencia>=120)
        {
            correctTime =  true;
        }
        
        return correctTime;
    }

    private boolean checkIfUrlIsCorrect(String url) throws IOException
    {
        boolean urlCorrect = false;
        URL u = new URL (url);
        HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection (); 
        huc.setRequestMethod ("GET");
        huc.connect () ; 
        int code = huc.getResponseCode();

        if(code != 404)
        {
            urlCorrect = true;
        }
        return urlCorrect;
    }

    private String horariosFinales(String horario, Date dateTime)
    {
        Date fechasFinales = new Date();
        fechasFinales = (Date) dateTime.clone();
        int hora = Integer.parseInt(horario.split(":")[0]);
        int minutos = Integer.parseInt(horario.split(":")[1]);
        fechasFinales.setHours(hora);
        fechasFinales.setMinutes(minutos);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.format(fechasFinales);
        return df.format(fechasFinales);
    }

    private List<Flight> obtenerHorariosVuelos(FechasVuelos fechaVuelo, Date departureDateTime, Date arrivalDateTime)
    {
        List<Flight> horariosVuelos = new ArrayList<Flight>();
        int minutosSalida = departureDateTime.getMinutes();
        int horaSalida = departureDateTime.getHours();
        int diaSalida = departureDateTime.getDate();

        int minutosLlegada = arrivalDateTime.getMinutes();
        int horaLlegada = arrivalDateTime.getHours();

        for(int i=0; i<fechaVuelo.getDays().size();i++)
        {
            if(fechaVuelo.getDays().get(i).getDay().equals(diaSalida))
            {
                //obtener las horas
                for(int j = 0; j<fechaVuelo.getDays().get(i).getFlights().size();j++)
                {
                    String arrivalTime = fechaVuelo.getDays().get(i).getFlights().get(j).getArrivalTime();
                    String departureTime = fechaVuelo.getDays().get(i).getFlights().get(j).getDepartureTime();

                    int horaDeparture = Integer.parseInt(departureTime.split(":")[0]);
                    int minutosDeparture = Integer.parseInt(departureTime.split(":")[1]);

                    int horaArrival = Integer.parseInt(arrivalTime.split(":")[0]);
                    int minutosArrival = Integer.parseInt(arrivalTime.split(":")[1]);

                    int horaIda = horaDeparture*60+minutosDeparture;
                    int nuestroHorarioSalida = horaSalida*60+minutosSalida;

                    int horaArrivalFinal = horaArrival *60 + minutosArrival;
                    int nuestroHorarioLlegada = horaLlegada*60+minutosLlegada;

                    if(nuestroHorarioSalida<=horaIda && nuestroHorarioLlegada>=horaArrivalFinal)
                    {
                        Flight fechasVuelo = new Flight();
                        fechasVuelo.setDepartureTime(fechaVuelo.getDays().get(i).getFlights().get(j).getDepartureTime());
                        fechasVuelo.setArrivalTime(fechaVuelo.getDays().get(i).getFlights().get(j).getArrivalTime());
                        horariosVuelos.add(fechasVuelo);
                    }
                }
            }
        }
        return horariosVuelos;
    }

    private boolean checkIfinterconnectedFlight (Ruta[] rutas, String departure, String arrival)
    {
        boolean isInterconnectedFligth = false;
        for(Ruta ruta : rutas)
        {
            if(ruta.getAirportFrom().equals(departure) && ruta.getAirportTo().equals(arrival) && ruta.getConnectingAirport() != null)
            {
                isInterconnectedFligth = true;
                break;
            }
        }
        return isInterconnectedFligth;
    }

    private List<String> searchConnectedFlights(Ruta[] rutas, String departure, String arrival)
    {
        List<String> conexionesVuelos = new ArrayList<String>();
        List<String> conexionesSalida = new ArrayList<String>();

        for(Ruta ruta : rutas)
        {
            if(ruta.getAirportFrom().equals(departure))
            {
                conexionesSalida.add(ruta.getAirportTo());
            }
        }

        for(Ruta ruta : rutas)
        {
            for(String vueloIntermedio : conexionesSalida)
            {
                if(ruta.getAirportFrom().equals(vueloIntermedio) && ruta.getAirportTo().equals(arrival))
                {
                    conexionesVuelos.add(vueloIntermedio);
                }
            }
        }
        return conexionesVuelos;
    }

    private String getInterconnectedFlight (Ruta[] rutas, String departure, String arrival)
    {
        String interconnectedFligth = null;
        for(Ruta ruta : rutas)
        {
            if(ruta.getAirportFrom().equals(departure) && ruta.getAirportTo().equals(arrival) && ruta.getConnectingAirport() != null)
            {
                interconnectedFligth = ruta.getConnectingAirport();
                break;
            }
        }
        return interconnectedFligth;
    }
}
