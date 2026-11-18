# Proyecto Final POO - Biblioteca

### Integrantes:
- Peña Ramirez Angel Antonio
- Salazar Díaz Jorge Otilio
- Tejeda Jiménez Alan Yahir

Dominio trabajado: Biblioteca

## Fases:  
El desarrollo se divide en entregas progresivas.

Entrega 1: Propuesta y UML (Fase 1: Inmersión en el dominio)

### 1. Investigación del Dominio y Reglas de Negocio:   
Una biblioteca académica o pública opera como un ecosistema dinámico donde convergen tres pilares fundamentales que garantizan el acceso ordenado y equitativo al conocimiento.

En primer lugar, la **gestión del acervo documental** trasciende el simple almacenamiento físico; implica un riguroso proceso de catalogación, clasificación taxonómica y control de existencias de libros, revistas especializadas, tesis y materiales de consulta restringida, lo que permite mantener actualizado el inventario y responder con precisión a las necesidades de consulta de la comunidad.

En segundo lugar, el **control de usuarios** establece un padrón seguro y segmentado que clasifica a los miembros según su perfil institucional (estudiantes, docentes o investigadores), regulando sus privilegios de acceso y manteniendo un registro transparente de su historial y estados de cuenta para prevenir pérdidas del patrimonio bibliográfico.

Finalmente, el **flujo de circulación de materiales** regula el pulso operativo de la institución a través de transacciones controladas: desde la validación de la elegibilidad del lector y la disponibilidad del ejemplar hasta la autorización de préstamos a domicilio o en sala, la recepción de devoluciones y el cálculo automatizado de sanciones por retraso, asegurando un ciclo transaccional fluido y sustentable.

#### Procesos principales del dominio: 
- Gestión de Acervo: Alta de nuevos ejemplares, consulta catalográfica, actualización de inventario y baja lógica por deterioro o extravío.   
- Gestión de Usuarios: Registro de lectores y personal, consulta de historial/adeudos, actualización de datos de contacto y suspensión o baja lógica.   
- Circulación (Préstamos y Devoluciones): Validación de disponibilidad y elegibilidad del solicitante, generación de préstamos con plazos diferenciados, recepción de devoluciones y cálculo automático de multas.   

#### Roles participantes:
- Bibliotecario: Administrador operativo encargado de registrar materiales y usuarios, autorizar préstamos, procesar devoluciones y gestionar bajas lógicas.   
- Lector (Estudiante / Docente): Usuario acreditado que consulta el catálogo y solicita el préstamo de materiales bajo condiciones diferenciadas según su perfil.

#### Reglas de Negocio (RN):
- RN-01 (Elegibilidad y stock del material): Solo pueden salir en préstamo externo materiales con estado activo (activo = true) y con al menos un ejemplar disponible (ejemplaresDisponibles > 0). Las obras clasificadas como LibroConsulta son exclusivamente para uso interno en sala y nunca salen a domicilio.   
- RN-02 (Límite de préstamos simultáneos): Un Estudiante puede mantener un máximo de 3 materiales prestados de forma simultánea, mientras que un Docente tiene un límite de 5 materiales.   
- RN-03 (Plazos de préstamo diferenciados): El plazo base de préstamo depende del tipo de material: 7 días naturales para Libro, 5 días para Tesis y 3 días para Revista. Si el lector es Docente, recibe 3 días adicionales sobre el plazo base.   
- RN-04 (Bloqueo por adeudos o retrasos): Ningún lector puede tramitar un nuevo préstamo si tiene préstamos vencidos sin devolver, multas pendientes por liquidar o su cuenta inactiva (activo = false).   
- RN-05 (Tarifa de multa diferenciada por material): Cada día natural de retraso genera una multa diaria distinta según la naturaleza del material: $10.00$ MXN/día para Libro, $15.00$ MXN/día para Tesis y $5.00$ MXN/día para Revista.   
- RN-06 (Integridad en bajas lógicas): No se permite dar de baja (activo = false) a un MaterialBibliografico con ejemplares actualmente prestados, ni a un Usuario con préstamos activos o adeudos pendientes.

