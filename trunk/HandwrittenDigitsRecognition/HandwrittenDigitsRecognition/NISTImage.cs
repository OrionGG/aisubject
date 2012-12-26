using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace HandwrittenDigitsRecognition
{


    class NISTImage
    {
        private const string ERROR_READING = "Error when reading file: ";

        public static List<byte[,]> Read(string sFileName)
        {
            List<byte[,]> oResult = new List<byte[,]>();
            //Fragmento de código que lee las imágenes
            using (BinaryReader oBinaryReader = new BinaryReader(File.OpenRead(sFileName)))
            {
                try
                {
                    int vacio1 = oBinaryReader.ReadByte();
                    int vacio2 = oBinaryReader.ReadByte();
                    int tipo = oBinaryReader.ReadByte();   //Valdrá 8
                    int dim = oBinaryReader.ReadByte();    //Valdrá 3
                    int numImagenes = oBinaryReader.ReadByte() << 24 | oBinaryReader.ReadByte() << 16 | oBinaryReader.ReadByte() << 8 | oBinaryReader.ReadByte();   //Valdrá 60000
                    int ancho = oBinaryReader.ReadByte() << 24 | oBinaryReader.ReadByte() << 16 | oBinaryReader.ReadByte() << 8 | oBinaryReader.ReadByte();         //Valdrá 28
                    int alto = oBinaryReader.ReadByte() << 24 | oBinaryReader.ReadByte() << 16 | oBinaryReader.ReadByte() << 8 | oBinaryReader.ReadByte();          //Valdra 28

                    //Este bucle lee todas las imágenes
                    for (int c = 0; c < numImagenes; c++)
                    {
                        byte[,] imagen = new byte[ancho, alto];
                        for (int y = 0; y < alto; y++)
                            for (int x = 0; x < ancho; x++)
                            {
                                int punto = oBinaryReader.ReadByte();
                                imagen[x, y] = (byte)punto;
                            }
                        //Aqui ya tenemos la imagen c-esima leída
                        oResult.Add(imagen);
                    }

                }
                catch (IOException ex)
                {
                    Console.WriteLine(ERROR_READING + ex.Message);
                }
            }
            
            return oResult;
        }

        
    }
}
