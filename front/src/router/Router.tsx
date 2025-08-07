// src/router/Router.tsx
import React from 'react';
import { createBrowserRouter } from 'react-router-dom';
import LoginPage from '../features/Login/LoginPage';
import SignupPage from "../features/Signup/SignupPage";

export const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/signup",
    element: <SignupPage />,
  },
]);
