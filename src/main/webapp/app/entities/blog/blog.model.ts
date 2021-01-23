import { IUser } from 'app/entities/user/user.model';

export interface IBlog {
  id?: number;
  name?: string | null;
  user?: IUser | null;
}

export class Blog implements IBlog {
  constructor(public id?: number, public name?: string | null, public user?: IUser | null) {}
}
