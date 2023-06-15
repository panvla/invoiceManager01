import { DataState } from '../enum/data-state';

export interface State<T> {
  dataState: DataState;
  appData?: T;
  error?: string;
}
