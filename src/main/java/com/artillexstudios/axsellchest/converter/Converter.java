package com.artillexstudios.axsellchest.converter;

import com.artillexstudios.axsellchest.converter.impl.ConverterVoidChest;
import com.artillexstudios.axsellchest.converter.impl.ConverterVoidChestV2;

import java.util.List;

public interface Converter {
    List<Converter> CONVERTERS = List.of(new ConverterVoidChest(), new ConverterVoidChestV2());

    String getName();

    void convert();

}
