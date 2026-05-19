package com.example.demo.Run.exeption;

public class RunNotFoundException extends RuntimeException
{
    public RunNotFoundException(Integer id)
    {
        super("Run con id  " + id + " non trovato");
    }
}
