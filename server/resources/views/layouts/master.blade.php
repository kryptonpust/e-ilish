<!doctype html>
<html lang="{{ app()->getLocale() }}">

<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
        <link rel="shortcut icon" sizes="32x32" href="{{ asset('bitmap.png') }}" type="image/png">

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">
    @if(Auth::user())
    <meta name="api_token" content="{{Auth::user()->api_token }}">
    @endif



    <title>{{ config('app.name', 'EELish') }}</title>

    <!-- Scripts -->
    
    <script src="{{ asset('js/three.js') }}" defer></script>
    <script src="{{ asset('js/OrbitControls.js') }}" defer></script>
    {{-- <script src="{{ asset('js/SVGLoader.js') }}" defer></script> --}}
    <script src="{{ asset('js/Water.js') }}" defer></script>
    <script src="{{ asset('js/Sky.js') }}" defer></script>
    <script src="{{ asset('js/WebGL.js') }}" defer></script>
    {{-- <script src="https://cloud.tinymce.com/5/tinymce.min.js"></script> --}}
    {{-- <script src="js/tinymce-vue.min.js"></script> --}}
    <script src="{{ asset('js/app.js') }}" defer></script> 
    <script src="{{ asset('js/draw.js') }}" defer></script>

    

    <!-- Fonts -->
    <link rel="dns-prefetch" href="//fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css?family=Nunito|Atma|Baloo+Da|Galada|Hind+Siliguri|Mina" rel="stylesheet">     
    {{-- <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU"
    crossorigin="anonymous"> --}}
    <!-- Styles -->
    <link href="{{ asset('css/app.css') }}" rel="stylesheet">
    <link href="{{ asset('css/all.css') }}" rel="stylesheet">

        <style>
        canvas {
            /* width: 100%; height: 100%; position: absolute; top: 0; left: 0; z-index: -9999; */
            width: 100vw; height: 100vh; display: block; position: fixed; top: 0; left: 0; z-index: -9999;
        }
    </style>
</head>

<body>
    <div id="app">
        @yield('content')
    </div>
</body>

</html>