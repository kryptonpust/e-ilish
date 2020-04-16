@extends('layouts.master') 
@section('content')

<div id="login">
    {{-- <div class="cloumns">
        <div class="column"> --}}
            <div class="box bg-transparent">
                <h1 class="title is-1 is-unselectable white">Login</h1>
                {{-- <div class="level">
                    <div class="level-left">
                        <div class="level-item">
                            <div class="title">Login</div>
                        </div>
                    </div>
                        <div class="level-right">
                            <div class="level-item">
                                <a class="button is-info icon-right" href="/">Home</a>
                            </div>
                        </div>
                </div>   --}}
                <div class="box bg-semi-transparent">
                    <form method="POST" action="{{ route('login') }}">
                        @csrf
                        <div class="field">
                            <p class="control has-icons-left has-icons-right">
                                <input id="email" class="input {{ $errors->has('email') ? ' is-danger' : '' }}" type="email" placeholder="Email" name="email"
                                    value="{{ old('email') }}" required autofocus>
                                <span class="icon is-small is-left">
                                    <i class="fas fa-envelope"></i>
                                </span>
                            </p>
                            @if ($errors->has('email'))
                            <p class="help is-danger">
                                <strong>{{ $errors->first('email') }}</strong>
                            </p> @endif
                        </div>
                        <div class="field">
                            <p class="control has-icons-left">
                                <input id="password" class="input {{ $errors->has('password') ? ' is-invalid' : '' }}" type="password" placeholder="Password"
                                    name="password" required>
                                <span class="icon is-small is-left">
                                    <i class="fas fa-lock"></i>
                                </span>
                            </p>
                            @if ($errors->has('password'))
                            <p class="help is-danger">
                                <strong>{{ $errors->first('password') }}</strong>
                            </p> @endif
                        </div>
                        <div class="field">
                            <p class="control">
                                <button type="submit" class="button is-success">
                                    Login
                                </button>
                            </p>
                        </div>
                    </form>
                </div>
            {{-- </div>
        </div> --}}
    </div>
</div>
@endsection