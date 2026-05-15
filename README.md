# Proyecto Amigos · Android Shell

Carcasa nativa Android que monta `https://proyectoamigos.com` dentro de una app instalable.

Este proyecto está **listo para compilarse en GitHub Actions** — no necesitas instalar Android Studio. Solo subes los archivos a un repo de GitHub y la nube te genera el APK.

---

## 🚀 Compilar el APK desde el celular (paso a paso)

### Paso 1 · Crear cuenta de GitHub

Si no tienes cuenta, entra a https://github.com/signup desde el navegador del celular. Es gratis. Te pide email, contraseña y nombre de usuario. ~3 minutos.

### Paso 2 · Crear un repositorio nuevo

1. Una vez dentro, toca el "+" arriba a la derecha → **New repository**
2. Nombre: `proyectoamigos-android` (o el que quieras)
3. Visibilidad: **Private** (para que nadie más vea el código)
4. **NO marques** ninguna de las opciones de "Initialize this repository with..."
5. Botón verde **Create repository**

### Paso 3 · Subir los archivos del proyecto

En la pantalla del repo recién creado, busca el link **"uploading an existing file"** (sale en el texto inicial). Toca ahí.

Desde el celular:

1. Descomprime el zip `proyectoamigos-android.zip` (apps como ZArchiver o el File Manager de Samsung/Xiaomi sirven)
2. En la pantalla de GitHub toca **"choose your files"**
3. Selecciona **todos** los archivos y carpetas descomprimidos
4. Espera a que carguen
5. Abajo en "Commit changes", deja el mensaje por defecto y toca **Commit changes**

> ⚠️ Importante: la carpeta oculta `.github` (con el workflow) **debe** subirse. Algunas apps esconden las carpetas que empiezan con punto. Activa "Mostrar archivos ocultos" en tu app de archivos.

### Paso 4 · Ver la compilación correr

1. Dentro del repo, toca la pestaña **Actions** (arriba)
2. Verás un workflow llamado "Build APK" ejecutándose (ícono amarillo girando)
3. Toca encima para ver el progreso en tiempo real
4. Espera ~5–8 minutos la primera vez
5. Cuando el ícono cambie a verde ✅, la compilación terminó

### Paso 5 · Descargar el APK al celular

1. En la pantalla del workflow ya terminado, baja hasta encontrar la sección **Artifacts**
2. Toca **proyectoamigos-debug-apk** → se descarga un ZIP al celular
3. Descomprime el ZIP → adentro está el archivo `.apk`
4. Toca el `.apk` → Android te dirá "Instalar desde fuente desconocida" → permite e instala
5. ¡Listo!

### Paso 6 · Recompilar después de cambios

- Edita archivos directamente desde la web de GitHub (botón lápiz en cada archivo)
- O sube versiones nuevas desde "Add file → Upload files"
- En cuanto hagas commit, GitHub Actions recompila solo
- También puedes recompilar manualmente: Actions → Build APK → botón **"Run workflow"**

---

## Qué incluye la app

- **WebView** que carga proyectoamigos.com con JS, cookies, DOM storage activos
- **Splash screen** nativo con el mark de marca (morado + cian sobre fondo oscuro)
- **Ícono adaptable** con el rombo + núcleo de la marca
- **Pull-to-refresh** con los tres colores de marca
- **Edge-to-edge** — la web aprovecha toda la pantalla, incluido el notch
- Barra de progreso morada sutil durante carga
- Botón atrás respeta el historial de la web
- Enlaces externos (otros dominios, `mailto:`, `tel:`) abren la app nativa correspondiente
- Estado preservado al rotar o cambiar de app

## Notas técnicas

- **applicationId**: `com.proyectoamigos.app`
- **minSdk**: 24 (Android 7.0) — cubre ~95% de dispositivos
- **targetSdk**: 34 (Android 14)
- **Tipo de APK que genera**: debug, firmado con la clave debug por defecto. Suficiente para instalar y probar en cualquier celular. Para Play Store necesitarás firmar con keystore propio.

## Cómo cambiar cosas comunes

| Quiero cambiar... | Archivo |
|---|---|
| La URL que carga la app | `app/src/main/java/com/proyectoamigos/app/MainActivity.kt` (variable `homeUrl`) |
| El nombre debajo del ícono | `app/src/main/res/values/strings.xml` |
| El ícono | `app/src/main/res/drawable/ic_launcher_foreground.xml` |
| El logo del splash | `app/src/main/res/drawable/splash_logo.xml` |
| Los colores | `app/src/main/res/values/colors.xml` |

## Próximos pasos cuando madure el proyecto

1. **APK firmado de release** — generar keystore propio, configurar `signingConfigs`
2. **Publicación en Play Store** — Google Play Console (US$25 único)
3. **Push notifications** — Firebase Cloud Messaging
4. **Deep links verificados** — `proyectoamigos.com/...` abre la app en vez del navegador
5. **Bridge JS ↔ Kotlin** — la web puede llamar código nativo (biometría, cámara)
