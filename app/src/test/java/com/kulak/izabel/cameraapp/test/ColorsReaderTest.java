package com.kulak.izabel.cameraapp.test;

import com.kulak.izabel.cameraapp.ColorsReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by i.kulakowska on 08/12/15.
 */
public class ColorsReaderTest {

    @Test
    public void shouldReadColorsFromCsvFile() {
        ColorsReader colorsReader = new ColorsReader();

        ColorsReader.readColors();

        assertEquals(44, colorsReader.getColorPicks().size());
    }

}