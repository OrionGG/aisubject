using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace HandwrittenDigitsRecognition
{
    class NISTLabel
    {
        public static List<int> Read(string sFileName)
        {
            List<int> lResutl = new List<int>();
            //Fragmento de código que lee las etiquetas correspondientes a cada imagen
            using (BinaryReader oBinaryReader = new BinaryReader(File.OpenRead(sFileName)))
            {
                try
                {
                    int vacio1 = oBinaryReader.ReadByte();
                    int vacio2 = oBinaryReader.ReadByte();
                    int tipo = oBinaryReader.ReadByte();  //Valdrá 8
                    int dim = oBinaryReader.ReadByte();   //Valdrá 1
                    int numEtiquetas = oBinaryReader.ReadByte() << 24 | oBinaryReader.ReadByte() << 16 | oBinaryReader.ReadByte() << 8 | oBinaryReader.ReadByte();   //Valdrá 60000

                    //Este bucle lee todas las etiquetas
                    for (int c = 0; c < numEtiquetas; c++)
                    {
                        int etiqueta = oBinaryReader.ReadByte();
                        //Aqui ya tenemos la etiqueta de la imagen c-esima leída
                        lResutl.Add(etiqueta);

                    }
                }
                catch (IOException ex)
                {
                }
            }

            return lResutl;
        }
    }
}
