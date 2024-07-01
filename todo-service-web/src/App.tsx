import React from 'react';
import { createRoot } from 'react-dom/client';
import Test from './Test';

const container = document.getElementById('root');
const root = createRoot(container as HTMLElement);

root.render(
  <React.StrictMode>
    <Test />
  </React.StrictMode>
);
