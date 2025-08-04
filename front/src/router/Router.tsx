// src/router/Router.tsx
import React from 'react';
import { createBrowserRouter } from 'react-router-dom';
import LoginPage from '../features/Login/LoginPage';

export const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
]);
