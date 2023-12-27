package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.LocationNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class TSVLocationsLoader {
    public static List<LocationNode> loadLocationsFromTSV(String rutaArchivo) throws IOException {
        List<LocationNode> localizaciones = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\t");
                if (partes.length == 2) {
                    String nombre = partes[1];
                    localizaciones.add(new LocationNode(nombre));
                }
            }
        }
        return localizaciones;
    }
}
