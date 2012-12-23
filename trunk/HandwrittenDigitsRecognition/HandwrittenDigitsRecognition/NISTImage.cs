using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace HandwrittenDigitsRecognition
{
    class NISTImage
    {
        public static List<byte[,]> Read(string sFileName)
        {
            List<byte[,]> oResult = new List<byte[,]>();
            //Fragmento de código que lee las imágenes

            try
            {
                BinaryReader oBinaryReader = new BinaryReader(File.OpenRead(sFileName));
                int vacio1 = oBinaryReader.ReadInt32();
                int vacio2 = oBinaryReader.ReadInt32();
                int tipo = oBinaryReader.ReadInt32();   //Valdrá 8
                int dim = oBinaryReader.ReadInt32();    //Valdrá 3
                int numImagenes = oBinaryReader.ReadInt32() << 24 | oBinaryReader.ReadInt32() << 16 | oBinaryReader.ReadInt32() << 8 | oBinaryReader.ReadInt32();   //Valdrá 60000
                int ancho = oBinaryReader.ReadInt32() << 24 | oBinaryReader.ReadInt32() << 16 | oBinaryReader.ReadInt32() << 8 | oBinaryReader.ReadInt32();         //Valdrá 28
                int alto = oBinaryReader.ReadInt32() << 24 | oBinaryReader.ReadInt32() << 16 | oBinaryReader.ReadInt32() << 8 | oBinaryReader.ReadInt32();          //Valdra 28

                //Este bucle lee todas las imágenes
                for (int c = 0; c < numImagenes; c++)
                {
                    byte[,] imagen = new byte[ancho, alto];
                    for (int y = 0; y < alto; y++)
                        for (int x = 0; x < ancho; x++)
                        {
                            int punto = oBinaryReader.ReadInt32();
                            imagen[x, y] = (byte)punto;
                        }
                    //Aqui ya tenemos la imagen c-esima leída
                    oResult.Add(imagen);
                }
            }
            catch (IOException ex)
            {
            }

            return oResult;
        }
    }
}
