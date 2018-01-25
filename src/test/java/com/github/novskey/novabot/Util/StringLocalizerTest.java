package com.github.novskey.novabot.Util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockitoAnnotations;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringLocalizerTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_GetLocalString__returns_localized_string() throws Exception {
        StringLocalizer.init(ResourceBundle.getBundle("Messages", Locale.ENGLISH),
                ResourceBundle.getBundle("TimeUnits", Locale.ENGLISH));
        String result = StringLocalizer.getLocalString("PauseCommand");
        Assert.assertEquals("!pause", result);
    }

    @Test
    public void test_GetLocalString__returns_localized_compound_string() throws Exception {
        StringLocalizer.init(ResourceBundle.getBundle("Messages", Locale.ENGLISH),
                ResourceBundle.getBundle("TimeUnits", Locale.ENGLISH));
        String result = StringLocalizer.getLocalString("HelpMessagePokemonCommands");
        assertThat(result, containsString("!addpokemon"));
    }

}

